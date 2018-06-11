package com.vn.ntsc.widget.livestream;

import android.app.Activity;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.views.livestream.MediaStream;

import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera3Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.vn.ntsc.widget.livestream.MediaConnection.StreamStatus.PAUSE;

/**
 * Created by nankai on 12/11/2017.
 */

public class MediaConnection implements MediaConnectionEvents {

    public enum ConnectState {
        CONNECTED, DENIED, ERROR, CLOSE
    }

    @StringDef({PAUSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StreamStatus {
        String PAUSE = "pause";
    }

    public static final class VideoSize {
        private final String label;
        private final int width;
        private final int height;

        public VideoSize(String label, int width, int height) {
            this.label = label;
            this.width = width;
            this.height = height;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final String TAG = "MediaConnection";

    private static final int REMOTE_MAX_COUNT = 4;

    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    public static final String LOCAL_MEDIA_STREAM_ID = "STREAM-" + UUID.randomUUID().toString();

    private String streamId;

    private Activity activity;
    private boolean isFrontFacing = true;

    private int rotate = -1;

    private final Map<String, MediaStream> remoteStreamMap = new HashMap<>();

    @Inject
    UserLiveStreamService mUserLiveStreamService;

    private WebSocketRTCClient wsClient;

    // WebRTC
    private PeerConnectionFactory pcFactory;
    private PeerConnection peer;
    private VideoSource localVideoSource;
    private VideoTrack localVideoTrack;
    private org.webrtc.MediaStream localMediaStream;

    private AudioTrack localAudioTrack;
    private AudioSource localAudioSource;
    private VideoCapturer videoCapturer;
    private LinkedList<PeerConnection.IceServer> iceServers;
    private Timer statsTimer;

    private Subject<Object> iceConnectionSubject = PublishSubject.create();
    private Subject<MediaStream> addStreamSubject = PublishSubject.create();
    private Subject<MediaStream> removeStreamSubject = PublishSubject.create();
    private Subject<String> peerErrorSubject = PublishSubject.create();

    private static final MediaConstraints mediaConstraints;

    static {
        mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
    }

    @Override
    public MediaConnection setWsClient(WebSocketRTCClient wsClient) {
        this.wsClient = wsClient;
        return this;
    }

    @Override
    public Observable<JSONObject> getWebSocketRTCSubject() {
        return wsClient.getWsClientSubject();
    }

    //--------------- Get Subject ---------------------------//

    @Override
    public Observable<Object> getIceConnectionSubject() {
        return iceConnectionSubject;
    }

    @Override
    public Observable<MediaStream> getAddStreamSubject() {
        return addStreamSubject;
    }

    @Override
    public Observable<MediaStream> getRemoveStreamSubject() {
        return removeStreamSubject;
    }

    @Override
    public Observable<String> getPeerErrorSubject() {
        return peerErrorSubject;
    }

    @Inject
    public MediaConnection(UserLiveStreamService userLiveStreamService) {
        this.mUserLiveStreamService = userLiveStreamService;
        this.iceServers = new LinkedList<>();
        statsTimer = new Timer();
    }

    @Override
    public void init(Activity activity, EglBase.Context eglBaseContext) {

        this.activity = activity;
        if (pcFactory == null) {
            // First, we initiate the PeerConnectionFactory with our application context and some options.
            PeerConnectionFactory.initializeAndroidGlobals(
                    activity,  // Context
                    true);  // Hardware Acceleration Enabled
            pcFactory = new PeerConnectionFactory(new PeerConnectionFactory.Options());
            pcFactory.setVideoHwAccelerationOptions(eglBaseContext, eglBaseContext);
        }
    }

    @Override
    public void setEvents(WebSocketRTCEvents events) {
        this.wsClient.setEvents(events);
    }

    @Override
    public boolean isFrontFacing() {
        return isFrontFacing;
    }

    @Override
    public void release() {
        LogUtils.i(TAG, "release");

        if (peer != null) {
            peer.dispose();
            peer = null;
        }
        LogUtils.i(TAG, "Closing audio source.");
        if (localAudioSource != null) {
            localAudioSource.dispose();
            localAudioSource = null;
        }
        LogUtils.i(TAG, "Stopping capture.");
        if (videoCapturer != null) {
            videoCapturer.dispose();
            videoCapturer = null;
        }

        LogUtils.i(TAG, "Closing video source.");
        if (localVideoSource != null) {
            localVideoSource.dispose();
            localVideoSource = null;
        }
    }

    @Override
    public Observable<MediaStream> startVideo(final VideoSize currentVideoSize, final String filePath, final SurfaceTextureHelper surfaceTextureHelper,
                                              final int videoFps) {
        checkOrientation();
        return Observable.create(new ObservableOnSubscribe<MediaStream>() {
            @Override
            public void subscribe(ObservableEmitter<MediaStream> emitter) throws Exception {

                if (filePath == null) {
                    // Returns front/ face device name
                    videoCapturer = createVideoCapturer();
                    if (videoCapturer == null) {
                        emitter.onError(new Throwable("camera open failed."));
                        return;
                    }
                } else {
                    //TODO
                }

                emitter.onNext(emitterMediaStreamFile(currentVideoSize, filePath, videoFps, surfaceTextureHelper));
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable<MediaStream> startVideo(final VideoSize currentVideoSize, final int videoFps) {
        checkOrientation();
        return Observable.create(new ObservableOnSubscribe<MediaStream>() {
            @Override
            public void subscribe(ObservableEmitter<MediaStream> emitter) throws Exception {

                // Returns front/ face device name
                videoCapturer = createVideoCapturer();

                if (videoCapturer == null) {
                    emitter.onError(new Throwable("camera open failed."));
                    return;
                }

                emitter.onNext(emitterMediaStreamCamera(currentVideoSize, videoFps));
                emitter.onComplete();
            }
        });
    }

    private MediaStream emitterMediaStreamCamera(final VideoSize currentVideoSize, final int videoFps) {

        // We start out with an empty MediaStream object, created with help from our PeerConnectionFactory
        //  Note that LOCAL_MEDIA_STREAM_ID can be any string
        streamId = LOCAL_MEDIA_STREAM_ID + System.currentTimeMillis();
        localMediaStream = pcFactory.createLocalMediaStream(streamId);
        localVideoSource = pcFactory.createVideoSource(videoCapturer);


        localVideoTrack = pcFactory.createVideoTrack(VIDEO_TRACK_ID + System.currentTimeMillis(), localVideoSource);
        localMediaStream.addTrack(localVideoTrack);
        localVideoTrack.setEnabled(true);

        // First we create an AudioSource then we can create our AudioTrack
        MediaConstraints audioConstraints = new MediaConstraints();
        localAudioSource = pcFactory.createAudioSource(audioConstraints);
        localAudioTrack = pcFactory.createAudioTrack(AUDIO_TRACK_ID + System.currentTimeMillis(), localAudioSource);
        localMediaStream.addTrack(localAudioTrack);
        localAudioTrack.setEnabled(false);

        videoCapturer.startCapture(currentVideoSize.width, currentVideoSize.height, videoFps);
        MediaStream mediaStream = new MediaStream(localMediaStream);
        mediaStream.setFrontFacing(isFrontFacing);

        return mediaStream;
    }

    private MediaStream emitterMediaStreamFile(final VideoSize currentVideoSize, final String filePath, final int videoFps, SurfaceTextureHelper surfaceTextureHelper) throws IOException {
        LogUtils.i(TAG, "Stream file --> " + filePath);

        FileVideoCapturer2 fileVideoCapturer = new FileVideoCapturer2(filePath);
        fileVideoCapturer.initialize(surfaceTextureHelper, activity, null);
        // We start out with an empty MediaStream object, created with help from our PeerConnectionFactory
        //  Note that LOCAL_MEDIA_STREAM_ID can be any string
        streamId = LOCAL_MEDIA_STREAM_ID + System.currentTimeMillis();
        localMediaStream = pcFactory.createLocalMediaStream(streamId);
        localVideoSource = pcFactory.createVideoSource(fileVideoCapturer);


        localVideoTrack = pcFactory.createVideoTrack(VIDEO_TRACK_ID + System.currentTimeMillis(), localVideoSource);
        localMediaStream.addTrack(localVideoTrack);
        localVideoTrack.setEnabled(true);

        // First we create an AudioSource then we can create our AudioTrack
        MediaConstraints audioConstraints = new MediaConstraints();
        localAudioSource = pcFactory.createAudioSource(audioConstraints);
        localAudioTrack = pcFactory.createAudioTrack(AUDIO_TRACK_ID + System.currentTimeMillis(), localAudioSource);
        localMediaStream.addTrack(localAudioTrack);
        localAudioTrack.setEnabled(false);

        fileVideoCapturer.startCapture(currentVideoSize.width, currentVideoSize.height, videoFps);
        MediaStream mediaStream = new MediaStream(localMediaStream);
        mediaStream.setFrontFacing(isFrontFacing);

        return mediaStream;
    }

    @Override
    public Observable<StatsReport[]> enableStatsEvents(final int periodMs) {
        LogUtils.i(TAG, "enableStatsEvents");
        return Observable.create(new ObservableOnSubscribe<StatsReport[]>() {
            @Override
            public void subscribe(final ObservableEmitter<StatsReport[]> emitter) throws Exception {
                statsTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (peer == null) {
                            return;
                        }
                        boolean success = peer.getStats(new StatsObserver() {
                            @Override
                            public void onComplete(final StatsReport[] reports) {
                                emitter.onNext(reports);
                            }
                        }, null);
                        if (!success) {
                            Log.e(TAG, "getStats() returns false!");
                        }
                    }
                }, 0, periodMs);
            }
        });
    }

    @Override
    public void stopVideo() {
        LogUtils.i(TAG, "stopVideo");

        stopCapture();

        if (localMediaStream != null) {
            if (peer != null) {
                peer.removeStream(localMediaStream);
            }

            localMediaStream = null;
            localVideoSource = null;
            localVideoTrack = null;
            localAudioTrack = null;
            localAudioSource = null;
        }
    }

    @Override
    public void checkOrientation() {
        WindowManager windowManager = activity.getWindowManager();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        int rotate = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                if (isFrontFacing) {
                    rotate = 270;
                } else {
                    rotate = 90;
                }
                break;
            case Surface.ROTATION_90:
                rotate = 0;
                break;
            case Surface.ROTATION_180:
                if (isFrontFacing) {
                    rotate = 90;
                } else {
                    rotate = 270;
                }
                break;
            case Surface.ROTATION_270:
                rotate = 180;
                break;
        }

        this.rotate = rotate;

        wsClient.sendCheckOrientation(this.rotate, this.streamId);
    }

    @Override
    public Observable<MediaStream> changeCamera(final VideoSize currentVideoSize,
                                                final int videoFps) {

        return Observable.create(new ObservableOnSubscribe<MediaStream>() {
            @Override
            public void subscribe(final ObservableEmitter<MediaStream> emitter) throws Exception {
                MediaConnection.this.isFrontFacing = !MediaConnection.this.isFrontFacing;
                MediaConnection.this.stopVideo();
                Disposable disposable = startVideo(currentVideoSize, videoFps)
                        .subscribe(
                                new Consumer<MediaStream>() {
                                    @Override
                                    public void accept(MediaStream stream) throws Exception {
                                        if (peer != null) {
                                            peer.addStream(stream.rtcMediaStream());
                                            wsClient.sendReOffer();
                                        }
                                        stream.setFrontFacing(isFrontFacing);
                                        emitter.onNext(stream);
                                        emitter.onComplete();
                                    }
                                },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable error) throws Exception {
                                        emitter.onError(error);
                                    }
                                });

                emitter.setDisposable(disposable);
            }
        });
    }

    @Override
    public void connectToRoom() {
        wsClient.connectToRoom();
    }

    @Override
    public void disconnect() {
        _disconnect();
    }
    //endregion - Public


    private void stopCapture() {
        Log.i(TAG, "stopCapture");

        if (videoCapturer == null) {
            return;
        }
        try {
            videoCapturer.stopCapture();
        } catch (InterruptedException e) {
            // ignore
            Log.e(TAG, "videoCapturer.stopCapture error.", e);
        }
    }

    private void _disconnect() {
        statsTimer.cancel();

        for (String streamId : remoteStreamMap.keySet()) {
            final org.webrtc.MediaStream stream = remoteStreamMap.get(streamId).rtcMediaStream();
            if (!stream.videoTracks.isEmpty()) {
                VideoTrack videoTrack = stream.videoTracks.getLast();
                stream.removeTrack(videoTrack);
            }
            if (!stream.audioTracks.isEmpty()) {
                AudioTrack audioTrack = stream.audioTracks.getLast();
                stream.removeTrack(audioTrack);
            }
        }

        if (peer != null) {
            peer.close();
            peer = null;
        }

        remoteStreamMap.clear();
    }

    @Override
    public void disconnectFromRoom() {
        wsClient.disconnectFromRoom();
    }

    @Override
    public void stopLiveStream() {
        wsClient.stopLiveStream();
    }

    // Event MediaConnectionEvents

    /**
     * Send chat join request to media server
     */
    @Override
    public void sendJoin() {
        iceServers.add(new PeerConnection.IceServer(BuildConfig.STUN_URL, BuildConfig.USERNAME, BuildConfig.CREDENTIAL));
        iceServers.add(new PeerConnection.IceServer(BuildConfig.TURN_URL, BuildConfig.USERNAME, BuildConfig.CREDENTIAL));
        PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);

        peer = pcFactory.createPeerConnection(configuration, mediaConstraints, new MediaConnection.PeerConnectionObserver(this));

        if (localMediaStream != null) {
            peer.addStream(localMediaStream);
        }

        // Create Offer
        peer.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sdp) {
                Log.i(TAG, "CREATE OFFER CREATE SUCCESS");
                wsClient.sendOfferSdp(sdp);
            }

            @Override
            public void onSetSuccess() {
                throw new UnsupportedOperationException("onSetSuccess");
            }

            @Override
            public void onCreateFailure(String s) {
                signalingError(s);
                disconnect();
            }

            @Override
            public void onSetFailure(String s) {
                throw new UnsupportedOperationException("onSetSuccess");
            }
        }, mediaConstraints);
    }

    @Override
    public void receiveOffer(String sdp) {
        // Set remoteDescription
        SessionDescription remoteDescription = new SessionDescription(SessionDescription.Type.OFFER, sdp);

        Log.i(TAG, "remoteDescription:" + remoteDescription.description);

        peer.setRemoteDescription(new SdpObserver() {

            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.e(TAG, "onCreateSuccess error");
                throw new UnsupportedOperationException("onCreateSuccess");
            }

            @Override
            public void onSetSuccess() {
                Log.i(TAG, "peer.setRemoteDescription onSetSuccess");
                makeAnswer();
            }

            @Override
            public void onCreateFailure(String s) {
                Log.e(TAG, "onCreateFailure error s:" + s);
                throw new UnsupportedOperationException("onCreateFailure s:" + s);
            }

            @Override
            public void onSetFailure(String s) {
                Log.e(TAG, "onSetFailure error s:" + s);
                _disconnect();
            }
        }, remoteDescription);
    }

    @Override
    public void makeAnswer() {
        peer.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(final SessionDescription sessionDescription) {

                final SessionDescription sdp = sessionDescription;

                Log.i(TAG, "peer.createAnswer onCreateSuccess");
                peer.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        Log.i(TAG, "peer.setLocalDescription onCreateSuccess");
                    }

                    @Override
                    public void onSetSuccess() {
                        Log.i(TAG, "peer.setLocalDescription onSetSuccess");
                        wsClient.sendAnswerSdp(sdp);
                        // In order to send the screen orientation to the server with checkOrientation, rotate is initialized here.
                        rotate = -1;
                    }

                    @Override
                    public void onCreateFailure(String s) {
                        Log.i(TAG, "peer.setLocalDescription onCreateFailure: " + s);
                    }

                    @Override
                    public void onSetFailure(String s) {
                        Log.i(TAG, "peer.setLocalDescription onSetFailure: " + s);
                        signalingError(s);
                        disconnect();
                    }
                }, sessionDescription);
            }

            @Override
            public void onSetSuccess() {
                Log.i(TAG, "peer.setLocalDescription onSetSuccess");
            }

            @Override
            public void onCreateFailure(String s) {
                Log.i(TAG, "peer.setLocalDescription onCreateFailure: " + s);
                signalingError(s);
                disconnect();
            }

            @Override
            public void onSetFailure(String s) {
                Log.i(TAG, "peer.setLocalDescription onSetFailure: " + s);
            }
        }, mediaConstraints);
    }

    @Override
    public void displaysOnTheTimeline() {
        wsClient.displaysOnTheTimeline();
    }

    private void signalingError(final String message) {
        peerErrorSubject.onNext(message);
    }
    //endregion - Signaling

    //region - Media
    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = createCameraCapturer(new Camera3Enumerator());
        if (videoCapturer == null) {
            Log.e(TAG, "Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {

        VideoCapturer capturer = null;

        if (this.isFrontFacing) {
            capturer = createFrontCameraCapturer(enumerator);
            if (capturer == null) {
                capturer = createBackCameraCapturer(enumerator);
            }
        } else {
            capturer = createBackCameraCapturer(enumerator);
            if (capturer == null) {
                capturer = createFrontCameraCapturer(enumerator);
            }
        }

        return capturer;
    }

    private VideoCapturer createFrontCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Log.i(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Log.i(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    private VideoCapturer createBackCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // Front facing camera not found, try something else
        Log.i(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Log.i(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }
    //endregion - Media

    @Override
    public void sendStatus(@StreamStatus String status) {
        wsClient.sendStatus(status);
    }

    /**
     * PeerConnection.Observer
     * This class handles events that occurred in the peer connection with the media server.
     */
    private static class PeerConnectionObserver implements PeerConnection.Observer {

        private MediaConnection parent;

        private PeerConnectionObserver(MediaConnection mediaConnection) {
            this.parent = mediaConnection;
        }

        @Override
        public void onAddStream(final org.webrtc.MediaStream rtcMediaStream) {
            LogUtils.i(TAG, "onAddStream id:" + rtcMediaStream.label());

            if (parent.remoteStreamMap.size() >= REMOTE_MAX_COUNT) {
                LogUtils.w(TAG, "over max remote.");
                return;
            }
            MediaStream mediaStream = new MediaStream(rtcMediaStream);
            mediaStream.setFrontFacing(parent.isFrontFacing);
            parent.remoteStreamMap.put(rtcMediaStream.label(), mediaStream);
            parent.addStreamSubject.onNext(mediaStream);
        }

        @Override
        public void onRemoveStream(final org.webrtc.MediaStream rtcMediaStream) {
            LogUtils.i(TAG, "onRemoveStream id:" + rtcMediaStream.label());
            String streamId = rtcMediaStream.label();
            parent.remoteStreamMap.remove(streamId);

            // Empty MediaStream onNext.
            // Be careful when you turn onNext the MediaStream that holds rtcMediaStream.
            parent.removeStreamSubject.onNext(new MediaStream(streamId));
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            LogUtils.i(TAG, "onSignalingChange. " + signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            LogUtils.i(TAG, "onIceConnectionChange. " + iceConnectionState);
            if (iceConnectionState == PeerConnection.IceConnectionState.COMPLETED) {
                parent.iceConnectionSubject.onNext(new Object());
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            LogUtils.i(TAG, "onIceConnectionReceivingChange. " + b);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            LogUtils.i(TAG, "onIceGatheringChange. " + iceGatheringState);
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.i(TAG, "onIceCandidate");
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
            LogUtils.i(TAG, "onIceCandidatesRemoved");
        }

        @Override
        public void onDataChannel(final DataChannel dc) {
            LogUtils.i(TAG, "New Data channel " + dc.label());

            dc.registerObserver(new DataChannel.Observer() {
                public void onBufferedAmountChange(long previousAmount) {
                    LogUtils.i(TAG, "Data channel buffered amount changed: " + dc.label() + ": " + dc.state());
                }

                @Override
                public void onStateChange() {
                    LogUtils.i(TAG, "Data channel state changed: " + dc.label() + ": " + dc.state());
                }

                @Override
                public void onMessage(final DataChannel.Buffer buffer) {
                    if (buffer.binary) {
                        LogUtils.i(TAG, "Received binary msg over " + dc);
                        return;
                    }
                    ByteBuffer data = buffer.data;
                    final byte[] bytes = new byte[data.capacity()];
                    data.get(bytes);
                    String strData = new String(bytes);
                    LogUtils.i(TAG, "Got msg: " + strData + " over " + dc);
                }
            });
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.i(TAG, "onRenegotiationNeeded");
        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, org.webrtc.MediaStream[] mediaStreams) {
            LogUtils.i(TAG, "onAddTrack");
        }
    }
}
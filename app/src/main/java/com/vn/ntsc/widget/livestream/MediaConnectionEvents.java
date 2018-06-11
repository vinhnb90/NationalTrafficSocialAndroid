package com.vn.ntsc.widget.livestream;

import android.app.Activity;

import com.vn.ntsc.widget.views.livestream.MediaStream;

import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceTextureHelper;

import io.reactivex.Observable;

/**
 * Created by nankai on 12/20/2017.
 */

public interface MediaConnectionEvents {

    /**
     * MediaService initialization processing
     *
     * @param activity       context (Activity instance)
     * @param eglBaseContext
     */
    void init(Activity activity, EglBase.Context eglBaseContext);

    void setEvents(WebSocketRTCEvents events);

    MediaConnection setWsClient(WebSocketRTCClient wsClient);

    Observable<JSONObject> getWebSocketRTCSubject();

    Observable<Object> getIceConnectionSubject();

    Observable<MediaStream> getAddStreamSubject();

    Observable<MediaStream> getRemoveStreamSubject();

    Observable<String> getPeerErrorSubject();

    /**
     * connect To Room
     */
    void connectToRoom();

    /**
     * Send chat join request to media server
     */
    void sendJoin();

    /**
     *
     * @param sdp
     */
    void receiveOffer(String sdp);

    /**
     * make Answer
     */
    void makeAnswer();

    /**
     * displays on the timeline
     */
    void displaysOnTheTimeline();

    void sendStatus(@MediaConnection.StreamStatus String status);

    boolean isFrontFacing();

    /**
     * Disconnect from the media server.
     */
    void disconnect();

    void disconnectFromRoom();

    void stopLiveStream();
    /**
     * MediaService release processing
     */
    void release();


    /**
     * Change the camera.
     */
    Observable<MediaStream> changeCamera(final MediaConnection.VideoSize currentVideoSize,
                                         final int videoFps);

    /**
     * check Orientation camera and send rotate to server
     */
    void checkOrientation();

    /**
     * Start the stream file.
     *
     * @return Camera stream Observable
     */
    Observable<MediaStream> startVideo(final MediaConnection.VideoSize currentVideoSize, final String filePath, SurfaceTextureHelper surfaceTextureHelper,
                                       final int videoFps);

    /**
     * Start the camera.
     *
     * @return
     */
    Observable<MediaStream> startVideo(final MediaConnection.VideoSize currentVideoSize, final int videoFps);

    /**
     * Stop the camera.
     */
    void stopVideo();

    Observable<StatsReport[]> enableStatsEvents(final int periodMs);

}

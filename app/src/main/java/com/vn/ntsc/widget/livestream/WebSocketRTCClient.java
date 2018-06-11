package com.vn.ntsc.widget.livestream;

import android.net.Uri;
import android.util.Log;

import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SessionDescription;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by nankai on 12/20/2017.
 * <p>
 * <p>To use: create an instance of this object (registering a message handler) and
 * call connectToRoom().  Once room connection is established
 * onConnectedToRoom() callback with room parameters is invoked.
 * Messages to other party (with local Ice candidates and answer SDP) can
 * be sent after WebSocket connection is established.
 */

public class WebSocketRTCClient implements AppRTCClient {

    private static final String TAG = "WebSocketRTCClient";

    private static final int MEDIA_API_VERSION = 1;

    private enum ConnectionState {NEW, CONNECTED, CLOSED, ERROR}

    private WebSocketChannelClient wsClient;
    private ConnectionState roomState;

    private WebSocketRTCEvents events;

    @Inject
    UserLiveStreamService mUserLiveStreamService;

    private CompositeDisposable disposables;
    private Subject<String> stringSubject = PublishSubject.create();
    private Subject<JSONObject> wsClientSubject = PublishSubject.create();

    @Inject
    public WebSocketRTCClient(UserLiveStreamService userLiveStreamService) {
        this.mUserLiveStreamService = userLiveStreamService;
        roomState = ConnectionState.NEW;
        disposables = new CompositeDisposable();
    }

    public Observable<JSONObject> getWsClientSubject() {
        return wsClientSubject;
    }

    public void setEvents(WebSocketRTCEvents events) {
        this.events = events;
    }

    // --------------------------------------------------------------------
    // AppRTCClient interface implementation.
    // Asynchronously connect to an AppRTC room URL using supplied connection
    // parameters, retrieves room parameters and connect to WebSocket server.
    @Override
    public void connectToRoom() {
        connectToRoomInternal();
    }

    @Override
    public void disconnectFromRoom() {
        disconnectFromRoomInternal();
    }

    @Override
    public void sendOfferSdp(final SessionDescription sdp) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "join");
            json.put("planb", "false");
            if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.VIEW)
                json.put("hash", mUserLiveStreamService.roomHash);
            json.put("capabilities", sdp.description);
            if (wsClient != null)
                wsClient.send(json.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendAnswerSdp(final SessionDescription sdp) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "answer");
            json.put("sdp", sdp.description);
            if (wsClient != null)
                wsClient.send(json.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendReOffer() {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "reoffer");
            if (wsClient != null)
                wsClient.send(json.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopLiveStream() {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "stop");
            if (wsClient != null)
                wsClient.send(json.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void displaysOnTheTimeline() {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "public");
            if (wsClient != null)
                wsClient.send(json.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendCheckOrientation(final int rotate, final String streamID) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "orientation");
            json.put("rotate", rotate);
            json.put("streamId", streamID);
            if (wsClient != null)
                wsClient.send(json.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendStatus(String status) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "streamStatus");
            json.put("status", status);
            if (wsClient != null)
                wsClient.send(json.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setObservers() {
        Disposable messageDisposable = wsClient.getMessageDisposable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(final String message) throws Exception {
                        try {
                            JSONObject json = new JSONObject(message);
                            wsClientSubject.onNext(json);
                            stringSubject.onNext(message);

                        } catch (JSONException e) {
                            Log.e(TAG, message);
                            throw new RuntimeException(e);
                        }
                    }
                });
        disposables.add(messageDisposable);

        Disposable stringSubjectDisposable = stringSubject.subscribeOn(Schedulers.io()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String message) throws Exception {
                try {
                    JSONObject json = new JSONObject(message);
                    final String type = json.getString("type");

                    // After successful auth, the server sends the type {ping}} to check whether the client socket is alive or not.
                    // If you do not receive a response from the client, the server will close the socket.
                    // Client needs to catch the "ping" signal every time the server requests and send feedback: {type: "pong"}
                    if (type.equals("ping")) {
                        JSONObject pjson = new JSONObject();
                        pjson.put("type", "pong");
                        wsClient.send(pjson.toString());
                        LogUtils.i(TAG, pjson.toString());
                        return;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, message);
                    throw new RuntimeException(e);
                }
            }
        });
        disposables.add(stringSubjectDisposable);

        Disposable socketDisposable = wsClient.geSocketDisposableDisposable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WebSocketChannelClient.WebSocketState>() {
                    @Override
                    public void accept(final WebSocketChannelClient.WebSocketState state) throws Exception {
                        switch (state) {
                            case DENIED:
                                events.onWebSocketDenied();
                                break;
                            case ERROR:
                                events.onWebSocketError();
                                break;
                            case CLOSED:
                                events.onWebSocketClose();
                                break;
                        }
                    }
                });
        disposables.add(socketDisposable);

        // All events are called by WebSocketChannelClient on a local looper thread
        // (passed to WebSocket client constructor).
        Disposable registerDisposable = wsClient.getRegisterDisposable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WebSocketChannelClient.WebSocketConnectionState>() {
                    @Override
                    public void accept(final WebSocketChannelClient.WebSocketConnectionState state) throws Exception {
                        if (state != WebSocketChannelClient.WebSocketConnectionState.CONNECTED) {
                            Log.w(TAG, "WebSocket register() in state " + state);
                            return;
                        }

                        try {
                            JSONObject json = new JSONObject();
                            json.put("type", "auth");
                            if (mUserLiveStreamService.mode == UserLiveStreamService.Mode.CHAT) {
                                json.put("hash", mUserLiveStreamService.userHash);//Room user hash token user
                                json.put("buzz_val", mUserLiveStreamService.buzzVal);
                                json.put("privacy", mUserLiveStreamService.privacy);
                                json.put("tag_list", new JSONArray(mUserLiveStreamService.tagList));
                            } else {
                                json.put("hash", mUserLiveStreamService.roomHash);//Room user hash token user
                            }
                            json.put("action", mUserLiveStreamService.mode);//Mode 1 owner or 2 view
                            wsClient.send(json.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        disposables.add(registerDisposable);
    }

    // Connects to room - function runs on a local looper thread.
    private void connectToRoomInternal() {
        Uri builtUri = Uri.parse(BuildConfig.MEDIA_WS_URL).buildUpon().appendQueryParameter("v", "" + MEDIA_API_VERSION).build();
        String connectionUrl = builtUri.toString();
        roomState = ConnectionState.NEW;

        wsClient = new WebSocketChannelClient();
        setObservers();

        signalingParametersReady(connectionUrl);
    }

    private void disconnectFromRoomInternal() {
        disposables.dispose();
        Log.d(TAG, "Disconnect. Room state: " + roomState);
        roomState = ConnectionState.CLOSED;
        if (wsClient != null) {
            wsClient.disconnect();
        }
    }

    private void signalingParametersReady(String connectionUrl) {

        LogUtils.i(TAG, "|------------ WebSocket RTC Client --------------\n"
                + "|room Hash  : " + mUserLiveStreamService.roomHash + "\n"
                + "|user Hash  : " + mUserLiveStreamService.userHash + "\n"
                + "|buzzVal    : " + mUserLiveStreamService.buzzVal + "\n"
                + "|buzzId     : " + mUserLiveStreamService.buzzId + "\n"
                + "|privacy    : " + mUserLiveStreamService.privacy + "\n"
                + "|tagList    : " + mUserLiveStreamService.tagList + "\n"
                + "|Action     : " + mUserLiveStreamService.mode + "\n"
                + "|Url        : " + connectionUrl + "\n"
                + "|------------ WebSocket RTC Client --------------");

        roomState = ConnectionState.CONNECTED;
        wsClient.connect(connectionUrl);
        wsClient.setState(WebSocketChannelClient.WebSocketConnectionState.REGISTERED);
    }

}

package com.vn.ntsc.widget.livestream;

import android.util.Log;

import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by nankai on 12/20/2017.
 */

public class WebSocketChannelClient extends WebSocketListener {
    private static final String TAG = "WebSocketChannelClient";
    /**
     * timeout in seconds
     */
    private static final int TIMEOUT = 15;

    private static final int CLOSE_TIMEOUT = 0;
    private WebSocketConnectionState state;
    private WebSocketState socketState;
    private String wsServerUrl;

    private Request request;
    private OkHttpClient client;
    private WebSocket ws;

    private boolean closeEvent;
    private final Object closeEventLock = new Object();


    private Subject<String> messageDisposable = PublishSubject.create();
    private Subject<WebSocketState> socketDisposable = PublishSubject.create();
    private Subject<WebSocketConnectionState> registerDisposable = PublishSubject.create();

    public Observable<String> getMessageDisposable() {
        return messageDisposable;
    }

    public Observable<WebSocketState> geSocketDisposableDisposable() {
        return socketDisposable;
    }

    public Observable<WebSocketConnectionState> getRegisterDisposable() {
        return registerDisposable;
    }

    /**
     * Possible WebSocket connection states.
     */
    public enum WebSocketConnectionState {
        NEW, CONNECTED, REGISTERED, CLOSED, ERROR
    }

    public enum WebSocketState {
        DENIED, CLOSED, ERROR
    }

    public WebSocketChannelClient() {
        socketState = WebSocketState.CLOSED;
        state = WebSocketConnectionState.NEW;
    }

    public void connect(final String wsUrl) {
        if (socketState == WebSocketState.CLOSED)
            state = WebSocketConnectionState.NEW;

        LogUtils.i(TAG, "Connecting WebSocket to: " + wsUrl);
        if (state != WebSocketConnectionState.NEW) {
            LogUtils.w(TAG, "WebSocket is already connected.");
            return;
        }
        wsServerUrl = wsUrl;
        closeEvent = false;
        try {

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            client = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .build();

            request = new Request.Builder()
                    .url(WebSocketChannelClient.this.wsServerUrl)
                    .addHeader("user-agent", "android-webrtc-client")
                    .build();

            ws = client.newWebSocket(request, this);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        Log.d(TAG, "WebSocket connection opened to: " + wsServerUrl);
        state = WebSocketConnectionState.CONNECTED;
        registerDisposable.onNext(state);
    }

    @Override
    public void onMessage(WebSocket webSocket, final String message) {
        super.onMessage(webSocket, message);
        LogUtils.i(TAG, "WSS -> receiver: " + message);
        if (state == WebSocketConnectionState.CONNECTED
                || state == WebSocketConnectionState.REGISTERED) {
            messageDisposable.onNext(message);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, final int code, String reason) {
        super.onClosing(webSocket, code, reason);
        Log.i(TAG, "WebSocketClient onClose. code:" + code + " reason:" + reason);
        socketState = WebSocketState.CLOSED;

        synchronized (closeEventLock) {
            closeEvent = true;
            closeEventLock.notify();
        }
        if (state != WebSocketConnectionState.CLOSED) {
            state = WebSocketConnectionState.CLOSED;
            socketDisposable.onNext(WebSocketState.CLOSED);
            // Disconnect if access to server is denied
            if (code == 1003) {
                socketDisposable.onNext(WebSocketState.DENIED);
                return;
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable ex, Response response) {
        super.onFailure(webSocket, ex, response);
        reportError(ex.getMessage());
    }

    public void send(String message) {
        LogUtils.i(TAG, "WSS -> request: " + message);
        switch (state) {
            case CONNECTED:
                // Store outgoing messages and send them after websocket client
                // is registered.
                ws.send(message);
                state = WebSocketChannelClient.WebSocketConnectionState.REGISTERED;
                return;
            case NEW:
            case ERROR:
            case CLOSED:
                Log.e(TAG, "WebSocket send() in error or closed state : " + message);
                return;
            case REGISTERED:
                ws.send(message);
                break;
        }
    }

    public void disconnect() {
        // Close WebSocket in CONNECTED or ERROR states only.
        if (state == WebSocketConnectionState.CONNECTED
                || state == WebSocketConnectionState.ERROR) {
            Log.w(TAG, "Disconnecting WebSocket.");
            state = WebSocketConnectionState.CLOSED;

            ws.close(1000, null);
            client.dispatcher().executorService().shutdown();

            // Wait for websocket close event to prevent websocket library from
            // sending any pending messages to deleted looper thread.
//            if (true) {
//                synchronized (closeEventLock) {
//                    while (!closeEvent) {
//                        try {
//                            closeEventLock.wait(CLOSE_TIMEOUT);
//                            break;
//                        } catch (InterruptedException e) {
//                            Log.e(TAG, "Wait error: " + e.toString());
//                        }
//                    }
//                }
//            }
        }
    }

    public void setState(WebSocketConnectionState state) {
        this.state = state;
        registerDisposable.onNext(state);
    }

    public WebSocketConnectionState getState() {
        return state;
    }

    private void reportError(final String errorMessage) {
        Log.e(TAG, Utils.nullToEmpty(errorMessage));
        if (state != WebSocketConnectionState.ERROR) {
            state = WebSocketConnectionState.ERROR;
            socketDisposable.onNext(WebSocketState.ERROR);
        }
    }
}

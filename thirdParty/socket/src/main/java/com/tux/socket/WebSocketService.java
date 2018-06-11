package com.tux.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tux.socket.models.Auth;
import com.tux.socket.models.Gift;
import com.tux.socket.models.Media;
import com.tux.socket.models.Message;
import com.tux.socket.models.MessageDeliveringState;
import com.tux.socket.models.Ping;
import com.tux.socket.models.SocketEvent;
import com.tux.socket.models.Sticker;
import com.tux.socket.models.Text;
import com.tux.socket.models.Typing;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.tux.socket.RxSocket.getSocketEvent;

/**
 * Created by dev22 on 11/23/17.
 * web socket service for chat and receive notification
 */
public class WebSocketService extends Service {
    private static final String TAG = "web_socket";
    /**
     * timeout in seconds
     */
    private static final int TIMEOUT = 15;

    /**
     * bind to get instance of service
     */
    private IBinder mBinder = new WebSocketBinder();
    private SocketManager socketManager = null;

    static final class SocketManager extends WebSocketListener {
        private final Request request;
        private OkHttpClient client;

        private final String userId;
        private final String token;
        /**
         * true when connected + send auth success
         *
         * @see #isConnecting
         */
        private boolean isReady = false;
        /**
         * store raw message if user is not auth, will send and clear as soon as possible when auth success
         */
        private List<String> sendLaterList = new ArrayList<>();
        private WebSocket ws;
        /**
         * true: connecting to socket
         *
         * @see #connectSocket(int)
         */
        private AtomicBoolean isConnecting = new AtomicBoolean(false);
        private Message sendingMessage;

        SocketManager(String socketIp, int socketPort, String token, String userId) {
            this.token = token;
            this.userId = userId;

            String socketUrl = "ws://" + socketIp + ":" + socketPort + "/ws/chat";
            request = new Request.Builder().url(socketUrl).build();
            connectSocket(TIMEOUT);
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, "open socket connection");
            getSocketEvent().onNext(new SocketEvent.Builder()
                    .eventType(SocketEvent.EVENT_OPEN)
                    .build());
            boolean isSuccess = webSocket.send(new Auth(token, userId).toJson());
            Log.d(TAG, "send auth: " + isSuccess + ", token=" + token + ", userId=" + userId);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG, "receive : " + text);
            Message msg = Message.parse(text);
            if (msg.getMessageType().equalsIgnoreCase(Auth.TYPE_AUTH)) {
                handleAuth(msg, webSocket);
            } else {
                String type = "";
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    type = jsonObject.getString("msg_type");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (type.equals(Ping.TYPE_PING)) {
                    String msgPing = new Ping(userId, token).toJson();
                    webSocket.send(msgPing);
                    Log.d(TAG, msgPing);
                }

                getSocketEvent().onNext(new SocketEvent.Builder()
                        .eventType(SocketEvent.EVENT_RECEIVE)
                        .message(msg)
                        .rawText(text)
                        .build());
            }
        }

        /**
         * handle auth event
         *
         * @param msg       received auth message
         * @param webSocket socket instance
         */
        private void handleAuth(Message msg, WebSocket webSocket) {
            if (msg.getValue().equalsIgnoreCase(Auth.VALUE_AUTH_SUCCESS)) {
                getSocketEvent().onNext(new SocketEvent.Builder()
                        .eventType(SocketEvent.EVENT_AUTH_SUCCESS)
                        .build());
                Log.d(TAG, "auth success.");
                isReady = true;
                isConnecting.set(false);

                for (String text1 : sendLaterList) {
                    Log.d(TAG, "sent cache msg: " + text1);
                    webSocket.send(text1);
                }
                Log.d(TAG, "clear cache list");
                sendLaterList.clear();
            } else {
                getSocketEvent().onNext(new SocketEvent.Builder()
                        .eventType(SocketEvent.EVENT_AUTH_FAIL)
                        .build());
                Log.d(TAG, "auth fail.");
                isReady = false;
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.d(TAG, "Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            getSocketEvent().onNext(new SocketEvent.Builder()
                    .eventType(SocketEvent.EVENT_CLOSE)
                    .build());

            Log.d(TAG, "Closing : " + code + " / " + reason);
            isReady = false;
            isConnecting.set(false);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            getSocketEvent().onNext(new SocketEvent.Builder()
                    .eventType(SocketEvent.EVENT_ERROR)
                    .message(sendingMessage)
                    .build());
            getSocketEvent().onNext(new SocketEvent.Builder()
                    .eventType(SocketEvent.EVENT_CLOSE)
                    .build());

            sendingMessage = null;

            isReady = false;
            isConnecting.set(false);
            t.printStackTrace();
        }

        /**
         * send message
         *
         * @param rawMessage json object to send
         */
        void sendMessage(String rawMessage) {
            Message message = Message.parse(rawMessage);
            message.setRawText(rawMessage);

            // ignore file type cause we need upload process
            String type = message.getMessageType();
            if (type != null) switch (type) {
                case Text.TYPE_TEXT:
                case Gift.TYPE_GIFT:
                // case Media.TYPE_FILE:
                case Sticker.TYPE_STICKER:
                    sendingMessage = message;

                    getSocketEvent().onNext(new SocketEvent.Builder()
                            .eventType(SocketEvent.EVENT_SENDING_MESSAGE)
                            .message(message)
                            .build());
                    break;
            }

            if (isReady) {
                boolean isSuccess = ws.send(rawMessage);
                if (isSuccess) sendingMessage = null;
                Log.d(TAG, isSuccess + "==sendMessage: " + rawMessage);
            } else {
                // list will be sent when auth success then clear
                if (type != null) switch (type) {
                    case Text.TYPE_TEXT:
                    case Gift.TYPE_GIFT:
                    case Media.TYPE_FILE:
                    case Sticker.TYPE_STICKER:
                        sendLaterList.add(rawMessage);
                        break;
                }

                if (!isConnecting.get()) {
                    connectSocket(TIMEOUT);
                }
            }
        }

        /**
         * connect to socket
         *
         * @param timeout timeout to connect
         */
        private void connectSocket(int timeout) {
            Log.d(TAG, "connectSocket: ");

            // fix RejectedExecutionException, cause call client.dispatcher().executorService().shutdown()
            // need re-create instance of client
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .build();
            ws = client.newWebSocket(request, this);
            isConnecting.set(true);
        }

        /**
         * close socket when possible
         */
        public void close() {
            ws.close(1000, null);
            client.dispatcher().executorService().shutdown();
        }
    }

    /**
     * create new instance and connect socket
     *
     * @param socketIp   socket address
     * @param socketPort port
     * @param token      token to auth
     * @param senderId   user id
     */
    public void connect(String socketIp, int socketPort, String token, String senderId) {
        if (socketManager == null || (!socketManager.isConnecting.get() && !socketManager.isReady)) {
            socketManager = new SocketManager(socketIp, socketPort, token, senderId);
        }
    }

    /**
     * send signal close socket to server
     */
    public void close() {
        if (socketManager != null) socketManager.close();
    }

    /**
     * send message, if not auth -> auth then send message later
     *
     * @param rawMessage json object to send
     * @throws RuntimeException when socket not enough info to connect
     */
    public void sendMessage(String rawMessage) {
        if (socketManager == null)
            Log.e(TAG, "sendMessage: pls call connect(String socketIp, int socketPort, String token, String senderId) first");
        else
            socketManager.sendMessage(rawMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socketManager != null)
            socketManager.close();
        Log.d(TAG, "onDestroy service");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");

        return mBinder;
    }

    /**
     * class for bind service, don't care about this class
     */
    public class WebSocketBinder extends Binder {
        public WebSocketService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WebSocketService.this;
        }
    }

    // socket additional

    /**
     * @return userId when connect(String socketIp, int socketPort, String token, String senderId)
     */
    private String getCurrentUserId() {
        if (socketManager != null) {
            return socketManager.userId;
        }
        Log.e(TAG, "userId not available: pls call connect(String socketIp, int socketPort, String token, String senderId) first");
        return null;
    }

    /**
     * send text
     *
     * @param toUserId to user id
     * @param text     message string
     * @see #sendMessage(String)
     */
    public void sendText(String msgId, String toUserId, String text, String type) {
        Text message = new Text(getCurrentUserId(), toUserId, text, type);
        message.setId(msgId);

        sendMessage(message.toJson());
    }

    /**
     * send start typing signal
     *
     * @see #sendMessage(String)
     */
    public void sendStartTyping(@NonNull String toUserId) {
        Typing message = new Typing(Typing.START_TYPING, getCurrentUserId(), toUserId);
        sendMessage(message.toJson());
    }

    /**
     * send stop typing signal
     *
     * @see #sendMessage(String)
     */
    public void sendStopTyping(@NonNull String toUserId) {
        Typing message = new Typing(Typing.STOP_TYPING, getCurrentUserId(), toUserId);
        sendMessage(message.toJson());
    }

    /**
     * <p>- mark message as read, when receive message via socket & chat activity isPresent (when onResume->true, onPause->false)</p>
     * <p>- mark all as read (call api: get_chat_history)</p>
     *
     * @param toUserId send to user id
     * @see #sendMessage(String)
     */
    public void sendMarkMessageAsRead(@NonNull String toUserId) {
        MessageDeliveringState message = new MessageDeliveringState(getCurrentUserId(), toUserId);
        sendMessage(message.toJson());
    }

    /**
     * send sticker
     *
     * @param id              message id
     * @param toUserId        receive user id
     * @param stickerCategory category (folder)
     * @param stickerName     sticker name (file name)
     * @see #sendMessage(String)
     */
    public void sendSticker(String id, @NonNull String toUserId, @NonNull String stickerCategory, @NonNull String stickerName, @NonNull String stickerUrl) {
        Sticker message = new Sticker(stickerCategory, stickerName, getCurrentUserId(), toUserId, stickerUrl);
        message.setId(id);

        sendMessage(message.toJson());
    }

    /**
     * send gift
     *
     * @param toUserId receiver id
     * @param giftId   gift id (= image id of gift on server)
     */
    public void sendGift(String id, @NonNull String toUserId, @NonNull String giftId) {
        Gift message = new Gift(giftId, getCurrentUserId(), toUserId);
        message.setId(id);

        sendMessage(message.toJson());
    }

    /**
     * send media file via socket
     *
     * @param id       message id
     * @param toUserId to user
     * @param listFile list file data response
     */
    public void sendMedia(String id, String toUserId, List<Media.FileBean> listFile) {
        Media media = new Media(getCurrentUserId(), toUserId, listFile);
        media.setId(id);
        sendMessage(media.toJson());
    }
}

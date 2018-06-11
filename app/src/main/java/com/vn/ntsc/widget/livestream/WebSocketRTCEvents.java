package com.vn.ntsc.widget.livestream;

/**
 * Created by nankai on 12/20/2017.
 * <p>
 * Callback interface for messages delivered on WebSocket.
 * All events are dispatched from a looper executor thread.
 */

public interface WebSocketRTCEvents {

    /**
     * socket close
     */
    void onWebSocketClose();

    /**
     * socket denied
     */
    void onWebSocketDenied();

    /**
     * socket error
     */
    void onWebSocketError();


    /**
     * Signaling error
     */
    void onSignalingError(String error);
}

package com.vn.ntsc.widget.socket;
import com.tux.socket.WebSocketService;

import javax.inject.Inject;

/**
 * Created by nankai on 1/4/2018.
 */

public final class RxSocket extends com.tux.socket.RxSocket {

    @Inject
    WebSocketService webSocketService;

    public RxSocket(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public WebSocketService getWebSocketService() {
        return webSocketService;
    }
}

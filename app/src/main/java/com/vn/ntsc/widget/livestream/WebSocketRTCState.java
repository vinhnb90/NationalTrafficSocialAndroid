package com.vn.ntsc.widget.livestream;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nankai on 3/7/2018.
 */
@StringDef({WebSocketRTCState.LIVE_STREAM_OPEN_SOCKET_LIVE_STREAM,
        WebSocketRTCState.LIVE_STREAM_OVER,
        WebSocketRTCState.LIVE_STREAM_JOIN_ME,
        WebSocketRTCState.LIVE_STREAM_OFFER,
        WebSocketRTCState.LIVE_STREAM_J0INED,
        WebSocketRTCState.LIVE_STREAM_VIEW,
        WebSocketRTCState.LIVE_STREAM_CLOSE,
        WebSocketRTCState.LIVE_STREAM_STATUS,
        WebSocketRTCState.LIVE_STREAM_CLOSE_SOCKET_LIVE_STREAM
})
@Retention(RetentionPolicy.SOURCE)
public @interface WebSocketRTCState {

    String LIVE_STREAM_OPEN_SOCKET_LIVE_STREAM = "open";
    String LIVE_STREAM_OVER = "over";
    String LIVE_STREAM_JOIN_ME = "joinme";
    String LIVE_STREAM_OFFER = "offer";
    String LIVE_STREAM_J0INED = "joined";
    String LIVE_STREAM_VIEW = "views";
    String LIVE_STREAM_CLOSE = "close";
    String LIVE_STREAM_STATUS = "streamStatus";
    String LIVE_STREAM_CLOSE_SOCKET_LIVE_STREAM = "close";

}

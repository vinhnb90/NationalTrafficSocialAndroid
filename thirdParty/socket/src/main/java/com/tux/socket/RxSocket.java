package com.tux.socket;

import com.tux.socket.models.SocketEvent;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by dev22 on 11/1/17.
 * to bind socket service
 */
@Deprecated
public class RxSocket {

    /**
     * log tag for debug
     */
    public static final String TAG = "socket";
    /**
     * emmit all socket event
     */
    private static PublishSubject<SocketEvent> subject = null;

    /**
     * will receive all socket event, you don't need to bind service
     * remember: subscribe and un-subscribe to avoid leak (onCreate | onDestroy)
     *
     * @return observable to receive all events of socket
     */
    public static PublishSubject<SocketEvent> getSocketEvent() {
        if (subject == null) {
            subject = PublishSubject.create();
        }
        return subject;
    }
}

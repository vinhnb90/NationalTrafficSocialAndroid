package com.tux.socket.models;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dev22 on 11/9/17.
 * listen events from socket
 */
@Keep
public class SocketEvent {
    /**
     * service is ready to use
     *
     * @see #EVENT_SERVICE_DISCONNECTED
     */
    public static final int EVENT_SERVICE_CONNECTED = 0;
    /**
     * unbind service
     *
     * @see #EVENT_SERVICE_CONNECTED
     */
    public static final int EVENT_SERVICE_DISCONNECTED = 1;
    /**
     * when open socket connection
     *
     * @see #EVENT_CLOSE
     */
    public static final int EVENT_OPEN = 2;
    /**
     * when connected to socket and receive auth status from server
     *
     * @see #EVENT_AUTH_FAIL
     */
    public static final int EVENT_AUTH_SUCCESS = 3;
    /**
     * when connected to socket and receive auth status from server
     *
     * @see #EVENT_AUTH_SUCCESS
     */
    public static final int EVENT_AUTH_FAIL = 4;
    /**
     * when receive msg
     *
     * @see #EVENT_AUTH_SUCCESS
     * @see #EVENT_AUTH_FAIL
     */
    public static final int EVENT_RECEIVE = 5;
    /**
     * close socket connection
     *
     * @see #EVENT_OPEN
     */
    public static final int EVENT_CLOSE = 6;
    /**
     * when sent error, lost internet connection, socket server close... -> call {@link #EVENT_CLOSE} intermediately need connect and auth again
     *
     * @see #EVENT_CLOSE
     */
    public static final int EVENT_ERROR = 7;
    /**
     * event sending message
     */
    public static final int EVENT_SENDING_MESSAGE = 8;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EVENT_SERVICE_CONNECTED, EVENT_SERVICE_DISCONNECTED, EVENT_OPEN, EVENT_CLOSE, EVENT_AUTH_SUCCESS, EVENT_AUTH_FAIL, EVENT_RECEIVE, EVENT_ERROR, EVENT_SENDING_MESSAGE})
    @interface EventType {
    }

    private int eventType;

    private Message message;

    private SocketEvent(Builder builder) {
        eventType = builder.eventType;
        message = builder.message;
    }

    /**
     * @return which event
     */
    @EventType
    public int getEventType() {
        return eventType;
    }

    /**
     * @return message of event, maybe null on event {@link #EVENT_SERVICE_DISCONNECTED}, {@link #EVENT_SERVICE_CONNECTED}
     */
    @Nullable
    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SocketEvent{" +
                "eventType=" + eventType +
                ", message=" + message +
                '}';
    }

    /**
     * builder patten for build socket event
     */
    public static final class Builder {
        private int eventType;
        private Message message;

        public Builder() {
        }

        public Builder eventType(@EventType int val) {
            eventType = val;
            return this;
        }

        public Builder message(@Nullable Message val) {
            message = val;
            return this;
        }

        public SocketEvent build() {
            return new SocketEvent(this);
        }

        public Builder rawText(String text) {
            message.setRawText(text);
            return this;
        }
    }
}

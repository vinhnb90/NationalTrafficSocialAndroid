package com.tux.socket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tux.socket.models.SocketEvent;

import static com.tux.socket.RxSocket.TAG;

/**
 * Created by dev22 on 1/3/18.
 * class to bind {@link WebSocketService} service
 */
public class SocketServiceConnection implements ServiceConnection {
    /**
     * true: bind service
     */
    private boolean isBound = false;
    private WebSocketService.WebSocketBinder socketBinder;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected: ");

        // fix crash on leakcanary
        // https://github.com/square/leakcanary/issues/91
        if (service instanceof WebSocketService.WebSocketBinder) {
            isBound = true;
            socketBinder = (WebSocketService.WebSocketBinder) service;
            RxSocket.getSocketEvent().onNext(new SocketEvent.Builder().eventType(SocketEvent.EVENT_SERVICE_CONNECTED).build());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected: ");
        socketBinder = null;
        RxSocket.getSocketEvent().onNext(new SocketEvent.Builder().eventType(SocketEvent.EVENT_SERVICE_DISCONNECTED).build());
    }

    /**
     * disconnect to service
     *
     * @param context to call {@link Context#unbindService(ServiceConnection)}
     */
    public void unbindService(@NonNull Context context) {
        if (isBound) {
            context.unbindService(this);
            isBound = false;
        }
    }

    /**
     * bind service and connect to socket automatically when have service instance
     *
     * @param context to call {@link Context#bindService(Intent, ServiceConnection, int)}
     */
    public void bindService(@NonNull Context context) {
        if (!isBound) {
            context.bindService(new Intent(context, WebSocketService.class), this, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * remember check isBound first
     *
     * @return socket service instance
     */
    public WebSocketService getSocketService() {
        return socketBinder.getService();
    }

    /**
     * @return true: if service is bound
     */
    public boolean isBound() {
        return isBound;
    }
}

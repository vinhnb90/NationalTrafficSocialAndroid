package com.vn.ntsc.di.modules;

import com.tux.socket.SocketServiceConnection;
import com.vn.ntsc.widget.socket.RxSocket;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nankai on 1/4/2018.
 */
@Module
public class SocketModule {
    
    private SocketServiceConnection socketServiceConnection;

    public SocketModule(SocketServiceConnection socketServiceConnection) {
        this.socketServiceConnection = socketServiceConnection;
    }

    @Provides
    @Singleton
    SocketServiceConnection provideSocketServiceConnection() {
        return socketServiceConnection;
    }

    @Provides
    @Singleton
    RxSocket provideRxSocket(SocketServiceConnection socketServiceConnection) {
        return new RxSocket(socketServiceConnection.getSocketService());
    }
}

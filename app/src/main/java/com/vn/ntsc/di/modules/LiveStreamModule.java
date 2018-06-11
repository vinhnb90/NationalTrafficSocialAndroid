package com.vn.ntsc.di.modules;

import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.widget.livestream.MediaConnection;
import com.vn.ntsc.widget.livestream.WebSocketRTCClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nankai on 12/6/2017.
 */
@Module
public class LiveStreamModule {

    @Provides
    @Singleton
    UserLiveStreamService provideUserLiveStreamService() {
        return new UserLiveStreamService();
    }

    @Provides
    WebSocketRTCClient provideWebSocketRTCClient(UserLiveStreamService userLiveStreamService) {
        return new WebSocketRTCClient(userLiveStreamService);
    }

    @Provides
    MediaConnection provideMediaService(UserLiveStreamService userLiveStreamService,WebSocketRTCClient webSocketRTCClient) {
        return new MediaConnection(userLiveStreamService).setWsClient(webSocketRTCClient);
    }
}

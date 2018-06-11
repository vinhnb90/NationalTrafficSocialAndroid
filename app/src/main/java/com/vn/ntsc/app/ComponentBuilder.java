package com.vn.ntsc.app;
import com.tux.socket.SocketServiceConnection;
import com.tux.socket.WebSocketService;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.di.components.DaggerLiveStreamComponent;
import com.vn.ntsc.di.components.DaggerModulesCommonComponent;
import com.vn.ntsc.di.components.DaggerTimelineComponent;
import com.vn.ntsc.di.components.LiveStreamComponent;
import com.vn.ntsc.di.components.ModulesCommonComponent;
import com.vn.ntsc.di.components.TimelineComponent;
import com.vn.ntsc.di.dependencies.AppComponent;
import com.vn.ntsc.di.dependencies.DaggerAppComponent;
import com.vn.ntsc.di.dependencies.DaggerMediaComponent;
import com.vn.ntsc.di.dependencies.MediaComponent;
import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.LiveStreamModule;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.di.modules.SocketModule;
import com.vn.ntsc.di.modules.TimelineModule;

/**
 * Created by nankai on 12/6/2017.
 */
public class ComponentBuilder implements IApplicationComponentBuilder {

    private AppController instance;
    private static AppComponent appComponent;
    private static MediaComponent mediaComponent;
    private static LiveStreamComponent liveStreamComponent;
    private static TimelineComponent timelineComponent;
    private static ModulesCommonComponent commonComponent;
    private static SocketServiceConnection socketServiceConnection;

    public ComponentBuilder(AppController instance) {
        socketServiceConnection = new SocketServiceConnection();
        if (instance != null)
            this.instance = instance;

        if (appComponent == null)
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this.instance))
                    .netModule(new NetModule(BuildConfig.SERVER_URL))
                    .socketModule(new SocketModule(socketServiceConnection))
                    .build();

        if (mediaComponent == null)
            mediaComponent = DaggerMediaComponent.builder()
                    .mediaModule(new MediaModule(BuildConfig.MEDIA_SERVER))
                    .appModule(new AppModule(this.instance))
                    .netModule(new NetModule(BuildConfig.SERVER_URL))
                    .build();

        if (liveStreamComponent == null)
            liveStreamComponent = DaggerLiveStreamComponent.builder()
                    .liveStreamModule(new LiveStreamModule())
                    .appModule(new AppModule(this.instance))
                    .netModule(new NetModule(BuildConfig.SERVER_URL))
                    .socketModule(new SocketModule(socketServiceConnection))
                    .mediaModule(new MediaModule(BuildConfig.MEDIA_SERVER))
                    .build();

        if (timelineComponent == null)
            timelineComponent = DaggerTimelineComponent.builder()
                    .timelineModule(new TimelineModule())
                    .appModule(new AppModule(this.instance))
                    .netModule(new NetModule(BuildConfig.SERVER_URL))
                    .socketModule(new SocketModule(socketServiceConnection))
                    .build();

        if (commonComponent == null)
            commonComponent = DaggerModulesCommonComponent.builder()
                    .appModule(new AppModule(this.instance))
                    .netModule(new NetModule(BuildConfig.SERVER_URL))
                    .socketModule(new SocketModule(socketServiceConnection))
                    .build();
    }

    public SocketServiceConnection getSocketServiceConnection() {
        return socketServiceConnection;
    }

    public WebSocketService getWebSocketService() {
        return socketServiceConnection.getSocketService();
    }

    public static ComponentBuilder install(AppController instance) {
        return new ComponentBuilder(instance);
    }

    @Override
    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public AppComponent getAppComponent(String url) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(instance))
                .netModule(new NetModule(url))
                .build();
    }

    @Override
    public AppComponent getAppComponent(String url, long connectTimeout, long readTimeout, long writeTimeout) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(instance))
                .netModule(new NetModule(url, connectTimeout, readTimeout, writeTimeout))
                .build();
    }

    @Override
    public AppComponent getAppComponent(String url, long connectTimeout, long readTimeout, long writeTimeout, boolean loggingPriority) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(instance))
                .netModule(new NetModule(url, connectTimeout, readTimeout, writeTimeout, loggingPriority))
                .build();
    }

    @Override
    public ModulesCommonComponent getModulesCommonComponent() {
        return commonComponent;
    }

    @Override
    public ModulesCommonComponent getModulesCommonComponent(String url) {
        return DaggerModulesCommonComponent.builder()
                .appModule(new AppModule(this.instance))
                .netModule(new NetModule(url))
                .build();
    }

    @Override
    public ModulesCommonComponent getModulesCommonComponent(String url, long connectTimeout, long readTimeout, long writeTimeout) {
        return DaggerModulesCommonComponent.builder()
                .appModule(new AppModule(this.instance))
                .netModule(new NetModule(url, connectTimeout, readTimeout, writeTimeout))
                .build();
    }

    @Override
    public ModulesCommonComponent getModulesCommonComponent(String url, long connectTimeout, long readTimeout, long writeTimeout, boolean loggingPriority) {
        return DaggerModulesCommonComponent.builder()
                .appModule(new AppModule(this.instance))
                .netModule(new NetModule(url, connectTimeout, readTimeout, writeTimeout, loggingPriority))
                .build();
    }

    @Override
    public MediaComponent getMediaComponent() {
        return mediaComponent;
    }

    @Override
    public MediaComponent getMediaComponent(MediaModule mediaModule) {
        return DaggerMediaComponent.builder()
                .appModule(new AppModule(instance))
                .netModule(new NetModule(BuildConfig.SERVER_URL))
                .mediaModule(mediaModule)
                .build();
    }

    @Override
    public MediaComponent getMediaComponent(NetModule netModule) {
        return DaggerMediaComponent.builder()
                .appModule(new AppModule(instance))
                .netModule(netModule)
                .mediaModule(new MediaModule(BuildConfig.MEDIA_SERVER))
                .build();
    }

    @Override
    public MediaComponent getMediaComponent(NetModule netModule, MediaModule mediaModule) {
        return DaggerMediaComponent.builder()
                .appModule(new AppModule(instance))
                .netModule(netModule)
                .mediaModule(mediaModule)
                .build();
    }

    @Override
    public LiveStreamComponent getLiveStreamComponent() {
        return liveStreamComponent;
    }

    @Override
    public TimelineComponent getTimelineComponent() {
        return timelineComponent;
    }
}

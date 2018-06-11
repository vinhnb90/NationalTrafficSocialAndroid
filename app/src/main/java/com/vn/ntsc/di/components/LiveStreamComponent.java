package com.vn.ntsc.di.components;

import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.LiveStreamModule;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.di.modules.SocketModule;
import com.vn.ntsc.ui.livestream.LiveStreamActivity;
import com.vn.ntsc.ui.livestream.LiveStreamPlayerFragment;
import com.vn.ntsc.ui.livestream.LiveStreamViewerFragment;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.subjects.Subject;

/**
 * Created by Robert on 2017 Nov 25
 */

@Singleton
@Component(modules = {
        LiveStreamModule.class,
        NetModule.class,
        SocketModule.class,
        AppModule.class,
        MediaModule.class
})
public interface LiveStreamComponent {

    //Token refresh
    Subject<String> onRefreshTokenSubject();

    void inject(LiveStreamActivity liveStreamActivity);

    void inject(LiveStreamViewerFragment liveStreamViewerFragment);

    void inject(LiveStreamPlayerFragment liveStreamPlayerFragment);
}

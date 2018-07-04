package com.vn.ntsc.di.components;

import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.di.modules.SocketModule;
import com.vn.ntsc.di.modules.TimelineModule;
import com.vn.ntsc.ui.comments.CommentActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.timeline.all.TimelineAllFragment;
import com.vn.ntsc.ui.timeline.fravorite.TimelineFavoriteFragment;
import com.vn.ntsc.ui.timeline.user.TimelineProfileFragment;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.subjects.Subject;

/**
 * Created by nankai on 12/13/2017.
 */
@Singleton
@Component(modules = {
        TimelineModule.class,
        NetModule.class,
        SocketModule.class,
        AppModule.class
})
public interface TimelineComponent {

    //Token refresh
    Subject<String> onRefreshTokenSubject();

    void inject(TimelineAllFragment timelineAllFragment);

    void inject(TimelineFavoriteFragment timelineFavoriteFragment);

    void inject(TimelineProfileFragment timelineProfileFragment);

    void inject(CommentActivity commentActivity);

    void inject(MyProfileActivity myProfileActivity);
}

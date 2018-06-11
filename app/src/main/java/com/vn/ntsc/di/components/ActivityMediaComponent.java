package com.vn.ntsc.di.components;

import com.vn.ntsc.di.dependencies.MediaComponent;
import com.vn.ntsc.di.modules.ActivityModule;
import com.vn.ntsc.di.scope.ActivityScope;
import com.vn.ntsc.ui.chat.meidiadetail.ChatMediaDetailActivity;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.ui.profile.edit.EditProfileActivity;
import com.vn.ntsc.ui.profile.media.albumDetail.MyAlbumDetailActivity;

import dagger.Component;

/**
 * Created by nankai on 16/8/7.
 */

@ActivityScope
@Component(dependencies = MediaComponent.class, modules = ActivityModule.class)
public interface ActivityMediaComponent {

    // inject Activity
    void inject(EditProfileActivity activity);

    void inject(TimelineMediaActivity timelineMediaActivity);

    void inject(MyAlbumDetailActivity myAlbumDetailActivity);

    void inject(ChatMediaDetailActivity chatMediaDetailActivity);

}

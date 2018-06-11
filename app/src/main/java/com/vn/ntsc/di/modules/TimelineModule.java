package com.vn.ntsc.di.modules;

import com.facebook.CallbackManager;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.ui.timeline.user.header.TimelineProfileViewHolder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nankai on 12/13/2017.
 */
@Module
public class TimelineModule {

    @Provides
    @Singleton
    UserInfoResponse provideUserInfo() {
        return new UserInfoResponse();
    }

    @Provides
    @Singleton
    CallbackManager provideCallbackManager(){
        return CallbackManager.Factory.create();
    }

    @Provides
    TimelineProfileViewHolder provideTimelineHeaderProfileViewHolder(UserInfoResponse infoResponse){
        return new TimelineProfileViewHolder(infoResponse);
    }
}

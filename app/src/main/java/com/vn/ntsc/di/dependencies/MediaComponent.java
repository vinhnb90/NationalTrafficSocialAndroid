package com.vn.ntsc.di.dependencies;


import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.repository.remote.ApiMediaService;
import com.vn.ntsc.repository.remote.ApiService;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.subjects.Subject;

/**
 * Created by nankai on 16/8/7.
 */

@Singleton
@Component( modules = {
        NetModule.class,
        AppModule.class,
        MediaModule.class})
public interface MediaComponent {
    //Token refresh
    Subject<String> onRefreshTokenSubject();

    ApiService getApiService();

    ApiMediaService getMediaService();
}

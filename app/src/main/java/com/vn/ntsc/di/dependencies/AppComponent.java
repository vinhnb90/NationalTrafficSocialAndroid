package com.vn.ntsc.di.dependencies;


import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.di.modules.SocketModule;
import com.vn.ntsc.repository.remote.ApiService;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.subjects.Subject;

/**
 * Created by nankai on 16/8/7.
 */

@Singleton
@Component(modules = {
        NetModule.class,
        SocketModule.class,
        AppModule.class
})
public interface AppComponent {
    //Token refresh
    Subject<String> onRefreshTokenSubject();

    ApiService getApiService();
}

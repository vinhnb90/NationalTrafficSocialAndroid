package com.vn.ntsc.di.modules;

import com.vn.ntsc.app.AppController;
import com.vn.ntsc.di.scope.ContextLife;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nankai on 16/8/7.
 */

@Module
public class AppModule {
    private final AppController application;

    public AppModule(AppController application) {
        this.application = application;
    }

    /*@Provides
    public MediaConnection provideMediaService() {
        return new MediaConnection();
    }*/

    @Provides
    @Singleton
    @ContextLife("Application")
    AppController provideApplicationContext() {
        return application;
    }
}

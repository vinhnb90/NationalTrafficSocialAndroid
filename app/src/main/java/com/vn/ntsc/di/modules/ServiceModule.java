package com.vn.ntsc.di.modules;

import android.app.Service;

import com.vn.ntsc.di.scope.ServiceScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nankai on 11/20/2017.
 */
@Module
public class ServiceModule {
    Service service;

    public ServiceModule(Service service) {
        this.service = service;
    }

    @Provides
    @ServiceScope
    Service provideService(){
        return this.service;
    }
}

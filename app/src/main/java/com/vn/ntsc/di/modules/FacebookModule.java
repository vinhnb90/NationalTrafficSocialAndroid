package com.vn.ntsc.di.modules;

import com.facebook.CallbackManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dev22 on 8/9/17.
 */
@Module
public class FacebookModule {
    @Provides
    public CallbackManager provideCallbackManager() {
        return CallbackManager.Factory.create();
    }
}

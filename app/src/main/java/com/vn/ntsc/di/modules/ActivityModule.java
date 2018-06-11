package com.vn.ntsc.di.modules;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.vn.ntsc.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nankai on 16/8/7.
 */

@Module
public class ActivityModule {
    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return mActivity;
    }
}

package com.vn.ntsc.services;

import android.app.IntentService;
import android.content.Intent;

import com.vn.ntsc.app.AppController;
import com.vn.ntsc.di.components.DaggerServiceComponent;
import com.vn.ntsc.di.components.DaggerServiceMediaComponent;
import com.vn.ntsc.di.components.ServiceComponent;
import com.vn.ntsc.di.components.ServiceMediaComponent;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.di.modules.ServiceModule;
import com.vn.ntsc.utils.LogUtils;

/**
 * Created by nankai on 11/21/2017.
 */

public abstract class BaseIntentService extends IntentService {
    private String TAG;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseIntentService(String name) {
        super(name);
        TAG = this.getClass().getSimpleName();
    }

    /**
     * 2 module {@link NetModule} và {@link MediaModule} đang được mặc định default
     * @return ServiceMediaComponent
     */
    public ServiceMediaComponent getServiceMediaComponent() {
        return DaggerServiceMediaComponent.builder()
                .mediaComponent(AppController.getComponent().getMediaComponent())
                .serviceModule(getServiceModule())
                .build();
    }

    /**
     * Dùng để cài đặt các thông số của {@link MediaModule}
     * @param mediaModule {@link MediaModule}
     * @return ServiceMediaComponent
     */
    public ServiceMediaComponent getServiceMediaComponent(MediaModule mediaModule) {
        return DaggerServiceMediaComponent.builder()
                .mediaComponent(AppController.getComponent().getMediaComponent(mediaModule))
                .serviceModule(getServiceModule())
                .build();
    }

    /**
     * Dùng để cài đặt các thông số của {@link NetModule}
     * @param netModule {@link NetModule}
     * @return ServiceMediaComponent
     */
    public ServiceMediaComponent getServiceMediaComponent(NetModule netModule) {
        return DaggerServiceMediaComponent.builder()
                .mediaComponent(AppController.getComponent().getMediaComponent(netModule))
                .serviceModule(getServiceModule())
                .build();
    }

    /**
     * Dùng để cài đặt các thông số của {@link NetModule} và {@link MediaModule}
     *
     * @param netModule {@link NetModule}
     * @param mediaModule {@link MediaModule}
     * @return ServiceMediaComponent
     */
    public ServiceMediaComponent getServiceMediaComponent(NetModule netModule, MediaModule mediaModule) {
        return DaggerServiceMediaComponent.builder()
                .mediaComponent(AppController.getComponent().getMediaComponent(netModule,mediaModule))
                .serviceModule(getServiceModule())
                .build();
    }

    /**
     * module {@link NetModule} đang được mặc định default
     * @return ServiceComponent
     */
    protected ServiceComponent getServiceComponent() {
        return DaggerServiceComponent.builder()
                .serviceModule(getServiceModule())
                .build();
    }

    /**
     * Dùng để cài đặt các thông số của {@link NetModule}
     *
     * @param url
     * @param connectTimeout
     * @param readTimeout
     * @param writeTimeout
     * @param loggingPriority
     * @return ServiceComponent
     */
    protected ServiceComponent getServiceComponent(String url, long connectTimeout, long readTimeout, long writeTimeout, boolean loggingPriority) {
        return DaggerServiceComponent.builder()
                .appComponent(AppController.getComponent().getAppComponent(url, connectTimeout, readTimeout, writeTimeout, loggingPriority))
                .serviceModule(getServiceModule())
                .build();
    }

    private ServiceModule getServiceModule() {
        return new ServiceModule(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtils.e(TAG,"Service onStart");
    }

    @Override
    public void onDestroy() {
        LogUtils.e(TAG,"Service onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e(TAG,"Service onUnbind");
        return super.onUnbind(intent);
    }
}

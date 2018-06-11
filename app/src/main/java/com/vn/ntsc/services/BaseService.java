package com.vn.ntsc.services;

import android.app.IntentService;

import com.vn.ntsc.di.modules.ServiceModule;

/**
 * Created by nankai on 11/21/2017.
 */

public abstract class BaseService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseService(String name) {
        super(name);
    }

    private ServiceModule getServiceModule() {
        return new ServiceModule(this);
    }

}

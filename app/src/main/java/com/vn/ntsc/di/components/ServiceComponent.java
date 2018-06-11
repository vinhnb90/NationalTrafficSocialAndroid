package com.vn.ntsc.di.components;

import com.vn.ntsc.di.dependencies.AppComponent;
import com.vn.ntsc.di.modules.ServiceModule;
import com.vn.ntsc.di.scope.ServiceScope;

import dagger.Component;

/**
 * Created by nankai on 11/20/2017.
 */

@ServiceScope
@Component(dependencies = AppComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

}

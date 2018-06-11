package com.vn.ntsc.di.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by nankai on 11/20/2017.
 */
@Scope
@Retention(RUNTIME)
public @interface ServiceScope {
}

package com.vn.ntsc.core.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.di.modules.FacebookModule;

import java.util.Arrays;

/**
 * implement facebook login for login/sign up activity
 *
 * @see #loginWithFacebook()
 * @see FacebookModule#provideCallbackManager()
 */
public abstract class LoginFacebookActivity<T extends BasePresenter> extends BaseActivity<T> implements FacebookCallback<LoginResult> {
    static final String EMAIL = "email";
    static final String PUBLIC_PROFILE = "public_profile";
    CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        // fix facebook sdk logout issue
        // https://stackoverflow.com/questions/34163207/facebook-logout-does-not-work-android-facebook-sdk-4
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        LoginManager.getInstance().registerCallback(callbackManager, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().unregisterCallback(callbackManager);
    }

    /**
     * action login with read public profile and read email permission
     */
    protected void loginWithFacebook() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(PUBLIC_PROFILE, EMAIL));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

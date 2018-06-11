package com.vn.ntsc.core.callback;

import android.content.Context;

/**
 * Created by nankai on 8/21/2017.
 */

public interface PresenterListener<T extends CallbackListener> {
    void attachView(T view);

    void detachView();

    void onAutoLogin(Context context);
}

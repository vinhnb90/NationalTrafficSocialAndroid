package com.vn.ntsc.ui.mediadetail.base;

import android.support.annotation.NonNull;

public interface BaseView<T> {
    //share presenter for components in a screen activity
    void setPresenter(@NonNull T presenter);
}

package com.vn.ntsc.ui.chat.sticker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ThoNh on 10/17/2017.
 */

public class Sticker implements Parcelable {
    public String pathSticker;

    public Sticker(String pathSticker) {
        this.pathSticker = pathSticker;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}

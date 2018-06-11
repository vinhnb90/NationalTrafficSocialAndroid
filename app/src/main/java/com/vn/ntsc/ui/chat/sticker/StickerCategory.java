package com.vn.ntsc.ui.chat.sticker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ThoNh on 10/17/2017.
 */

public class StickerCategory implements Parcelable{

    public Sticker source;

    public ArrayList<Sticker> mLstSticker;

    public StickerCategory(Sticker source, ArrayList<Sticker> mLstSticker) {
        this.source = source;
        this.mLstSticker = mLstSticker;
    }

    protected StickerCategory(Parcel in) {
    }

    public static final Creator<StickerCategory> CREATOR = new Creator<StickerCategory>() {
        @Override
        public StickerCategory createFromParcel(Parcel in) {
            return new StickerCategory(in);
        }

        @Override
        public StickerCategory[] newArray(int size) {
            return new StickerCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}

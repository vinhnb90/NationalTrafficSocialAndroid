package com.vn.ntsc.repository.model.emoji;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducng on 12/13/2017.
 */

public class EmojiReponse extends ServerResponse implements Parcelable {
    /**
     * data : []
     * code : 0
     */
    @SerializedName("data")
    public List<EmojiCategory> data;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.data);
    }

    public EmojiReponse() {
    }

    protected EmojiReponse(Parcel in) {
        this.data = new ArrayList<EmojiCategory>();
        in.readList(this.data, EmojiCategory.class.getClassLoader());
    }

    public static final Creator<EmojiReponse> CREATOR = new Creator<EmojiReponse>() {
        @Override
        public EmojiReponse createFromParcel(Parcel source) {
            return new EmojiReponse(source);
        }

        @Override
        public EmojiReponse[] newArray(int size) {
            return new EmojiReponse[size];
        }
    };
}

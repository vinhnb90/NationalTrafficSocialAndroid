package com.vn.ntsc.repository.model.conversation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.ArrayList;

/**
 * Created by ThoNH on 25/08/2017.
 */

public class ConversationResponse extends ServerResponse {
    @SerializedName("data")
    public ArrayList<ConversationItem> datas;

    public ConversationResponse() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.datas);
    }

    protected ConversationResponse(Parcel in) {
        super(in);
        this.datas = in.createTypedArrayList(ConversationItem.CREATOR);
    }

    public static final Parcelable.Creator<ConversationResponse> CREATOR = new Parcelable.Creator<ConversationResponse>() {
        @Override
        public ConversationResponse createFromParcel(Parcel source) {
            return new ConversationResponse(source);
        }

        @Override
        public ConversationResponse[] newArray(int size) {
            return new ConversationResponse[size];
        }
    };
}

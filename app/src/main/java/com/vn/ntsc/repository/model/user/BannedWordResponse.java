package com.vn.ntsc.repository.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

public class BannedWordResponse extends ServerResponse {
    @SerializedName("data")
    public
    BannedObject data;

    public static class BannedObject implements android.os.Parcelable {
        @SerializedName("list")
        private List<String> list;
        @SerializedName("version")
        private int version;

        public List<String> getList() {
            return list;
        }

        public int getVersion() {
            return version;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(this.list);
            dest.writeInt(this.version);
        }

        public BannedObject() {
        }

        protected BannedObject(Parcel in) {
            this.list = in.createStringArrayList();
            this.version = in.readInt();
        }

        public static final Creator<BannedObject> CREATOR = new Creator<BannedObject>() {
            @Override
            public BannedObject createFromParcel(Parcel source) {
                return new BannedObject(source);
            }

            @Override
            public BannedObject[] newArray(int size) {
                return new BannedObject[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public BannedWordResponse() {
    }

    protected BannedWordResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(BannedObject.class.getClassLoader());
    }

    public static final Parcelable.Creator<BannedWordResponse> CREATOR = new Parcelable.Creator<BannedWordResponse>() {
        @Override
        public BannedWordResponse createFromParcel(Parcel source) {
            return new BannedWordResponse(source);
        }

        @Override
        public BannedWordResponse[] newArray(int size) {
            return new BannedWordResponse[size];
        }
    };
}

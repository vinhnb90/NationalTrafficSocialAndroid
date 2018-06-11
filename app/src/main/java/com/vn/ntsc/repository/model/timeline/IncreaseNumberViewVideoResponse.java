package com.vn.ntsc.repository.model.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by ThoNh on 3/7/2018.
 */

public class IncreaseNumberViewVideoResponse extends ServerResponse{

    /**
     * data : {"view_number":1}
     */

    @SerializedName("data")
    public DataBean data;

    public static class DataBean implements Parcelable{
        /**
         * view_number : 1
         */

        @SerializedName("view_number")
        public int viewNumber;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.viewNumber);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.viewNumber = in.readInt();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
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

    public IncreaseNumberViewVideoResponse() {
    }

    protected IncreaseNumberViewVideoResponse(Parcel in) {
        super(in);
        this.data = in.readParcelable(DataBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<IncreaseNumberViewVideoResponse> CREATOR = new Parcelable.Creator<IncreaseNumberViewVideoResponse>() {
        @Override
        public IncreaseNumberViewVideoResponse createFromParcel(Parcel source) {
            return new IncreaseNumberViewVideoResponse(source);
        }

        @Override
        public IncreaseNumberViewVideoResponse[] newArray(int size) {
            return new IncreaseNumberViewVideoResponse[size];
        }
    };
}

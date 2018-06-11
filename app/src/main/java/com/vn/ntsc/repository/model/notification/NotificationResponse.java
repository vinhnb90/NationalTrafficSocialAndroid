package com.vn.ntsc.repository.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

/**
 * Created by ThoNh on 8/30/2017.
 */

/*
    sample response
    {
  "data":[
    {
      "is_seen":0,
      "video_call_waiting":true,
      "noti_id":"599fd444e4b070a829196afc",
      "time":"20170825073948",
      "noti_user_id":"57a80e3de4b062b2d356ebb5",
      "voice_call_waiting":true,
      "noti_type":19,
      "noti_user_name":"namham",
      "is_read":1,
      "last_login":"20170829042432",
      "dist":14162.0,
      "noti_buzz_id":"599fd444e4b0b63d83544885"
    },
    {
      "is_seen":0,
      "video_call_waiting":true,
      "noti_id":"599e7e88e4b070a8291969b6",
      "time":"20170824072144",
      "noti_user_id":"5772252b0cf25a8a3c869f42",
      "voice_call_waiting":true,
      "noti_type":28,
      "is_read":1,
      "last_login":"20170830024526",
      "dist":0.0,
      "noti_buzz_id":"5848b845e4b0f60c98d52780"
    }
        ]
    }
 */

public class NotificationResponse extends ServerResponse {

    @SerializedName("data")
    public List<NotificationItem> data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.data);
    }

    public NotificationResponse() {
    }

    protected NotificationResponse(Parcel in) {
        super(in);
        this.data = in.createTypedArrayList(NotificationItem.CREATOR);
    }

    public static final Parcelable.Creator<NotificationResponse> CREATOR = new Parcelable.Creator<NotificationResponse>() {
        @Override
        public NotificationResponse createFromParcel(Parcel source) {
            return new NotificationResponse(source);
        }

        @Override
        public NotificationResponse[] newArray(int size) {
            return new NotificationResponse[size];
        }
    };
}

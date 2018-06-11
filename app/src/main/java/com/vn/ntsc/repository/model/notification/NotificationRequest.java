package com.vn.ntsc.repository.model.notification;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 8/30/2017.
 */

// sample request: {"take":20,"version":3,"api":"lst_noti","token":"f28132d6-b10d-4509-9e67-d894cad317fe"}

public class NotificationRequest extends ServerRequest {

    @SerializedName("take")
    int take;
    @SerializedName("version")
    int version = 3;
    @SerializedName("time_stamp")
    public String timeStamp;

    public NotificationRequest( String token,int take) {
        super("lst_noti");
        this.token = token;
        this.take = take;
    }

    public NotificationRequest( String timeStamp, String token,int take) {
        super("lst_noti");
        this.timeStamp = timeStamp;
        this.token = token;
        this.take = take;
    }
}

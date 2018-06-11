package com.vn.ntsc.repository.model.installcount;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by nankai on 8/3/2017.
 */

public class InstallCountRequest extends ServerRequest {

    // int : 0 : IOS | 1: Android
    @SerializedName("device_type")
    private int device_type;
    @SerializedName("unique_number")
    private String unique_number;


    public InstallCountRequest(int device_type, String unique_number) {
        super("install_application");
        this.device_type = device_type;
        this.unique_number = unique_number;
    }
}

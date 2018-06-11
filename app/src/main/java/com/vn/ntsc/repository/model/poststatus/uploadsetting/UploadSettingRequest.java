package com.vn.ntsc.repository.model.poststatus.uploadsetting;


import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by Robert on 2017 Oct 24.
 */
public class UploadSettingRequest extends ServerRequest {

    public UploadSettingRequest(String token) {
       super("get_upload_setting");
        this.token = "";
    }
}
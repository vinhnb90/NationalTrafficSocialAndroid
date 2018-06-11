package com.vn.ntsc.repository.model.chat;

import okhttp3.MultipartBody;

/**
 * Created by ducng on 11/16/2017.
 */

public class UploadFileRequest {
    MultipartBody.Builder builder;

    public UploadFileRequest(MultipartBody.Builder builder) {
        this.builder = builder;
    }
}

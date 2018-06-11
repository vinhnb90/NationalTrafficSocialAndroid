package com.vn.ntsc.services;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static com.vn.ntsc.services.UserLiveStreamService.Mode.CHAT;
import static com.vn.ntsc.services.UserLiveStreamService.Mode.VIEW;
import static com.vn.ntsc.services.UserLiveStreamService.TypeView.STREAM_CAMERA;
import static com.vn.ntsc.services.UserLiveStreamService.TypeView.STREAM_FILE;

/**
 * Created by nankai on 12/7/2017.
 */

public class UserLiveStreamService {
    private static final String TAG = "UserLiveStreamService";

    @IntDef({CHAT, VIEW})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Mode {
        int CHAT = 1;
        int VIEW = 2;
    }

    @IntDef({STREAM_CAMERA, STREAM_FILE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TypeView {
        int STREAM_CAMERA = 1;
        int STREAM_FILE = 2;
    }

    public String buzzId;
    public String userId;
    public String userHash;
    public String roomHash;
    public @Mode
    int mode;
    public String buzzVal;
    public int privacy;
    public List<String> tagList;
    public int viewNumber;
    public int startTime;
    public @TypeView int typeView;
}

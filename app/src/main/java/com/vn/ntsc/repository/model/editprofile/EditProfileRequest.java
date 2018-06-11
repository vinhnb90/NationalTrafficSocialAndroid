package com.vn.ntsc.repository.model.editprofile;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by dev22 on 8/24/17.
 * request to update profile
 */
public class EditProfileRequest extends ServerRequest {
    @SerializedName("abt")
    public String abt = "";
    // 0: No | 1: Yes
    @SerializedName("auto_region")
    public int autoRegion = 0;

    @SerializedName("body_type")
    public int bodyType;

    // int: (Optional) ( -1 to 8)
    @SerializedName("cup")
    public int cup;

    // int: (Optional) : ( -1 to 12)
    @SerializedName("cute_type")
    public int cuteType;

    // 0: nam, 1: nu
    @SerializedName("gender")
    public int gender;

    @SerializedName("hobby")
    public String hobby;

    @SerializedName("join_hours")
    public int joinHours = 0;

    @SerializedName("job")
    public int job;

    @SerializedName("region")
    public int region;

    @SerializedName("user_name")
    public String userName = "";

    @SerializedName("email")
    public String email;

    // yyyyMMdd
    @SerializedName("bir")
    public String birthday;

    // identify number, visa no
    @SerializedName("id_number")
    public String identifyNumber;

    @SerializedName("phone_number")
    public String phoneNo;

    // when user upload avatar success => assign avatar id
    @SerializedName("ava_id")
    public String avatarId;

    public EditProfileRequest(String name, int bodyType, int gender, String hobby, int job,
                              int region, String token, String email, String birthday,
                              String identifyNumber, String phoneNo, String abt) {
        super("upd_user_inf");
        userName = name;
        this.bodyType = bodyType;
        this.gender = gender;
        this.hobby = hobby;
        this.job = job;
        this.region = region;
        this.token = token;
        this.email = email;
        this.birthday = birthday;
        this.identifyNumber = identifyNumber;
        this.phoneNo = phoneNo;
        this.abt = abt;
    }
}

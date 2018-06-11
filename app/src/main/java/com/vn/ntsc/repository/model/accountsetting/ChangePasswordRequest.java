package com.vn.ntsc.repository.model.accountsetting;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 10/23/2017.
 */

public class ChangePasswordRequest extends ServerRequest {

    @SerializedName("new_pwd")
    public String newPassword ;

    @SerializedName("old_pwd")
    public String oldPassword;

    @SerializedName("original_pwd")
    public String originPassword;

    public ChangePasswordRequest(String newPassword, String oldPassword, String originPassword, String token) {
        super("chg_pwd");
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        this.originPassword = originPassword;
        this.token = token;
    }
}

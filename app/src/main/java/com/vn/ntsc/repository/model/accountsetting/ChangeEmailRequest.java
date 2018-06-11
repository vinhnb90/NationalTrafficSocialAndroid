package com.vn.ntsc.repository.model.accountsetting;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by ThoNh on 10/9/2017.
 */

public class ChangeEmailRequest extends ServerRequest {

    @SerializedName("email")
    public String email;

    @SerializedName("original_pwd")
    public String originalPassword;

    @SerializedName("new_pwd")
    public String newPasswordEncrypt;

    @SerializedName("old_pwd")
    public String oldPasswordEncrypt;

    public ChangeEmailRequest(String email, String originalPassword, String newPasswordEncrypt, String oldPasswordEncrypt, String token) {
        super("change_email");
        this.email = email;
        this.originalPassword = originalPassword;
        this.newPasswordEncrypt = newPasswordEncrypt;
        this.oldPasswordEncrypt = oldPasswordEncrypt;
        this.token = token;
    }
}

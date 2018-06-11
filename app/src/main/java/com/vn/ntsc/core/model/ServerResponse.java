package com.vn.ntsc.core.model;

import android.os.Parcel;
import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.vn.ntsc.core.model.ServerResponse.DefinitionCode.*;

/**
 * @see java.io.Serializable
 * <p>
 * Created by nankai on 11/24/2016.
 */

public class ServerResponse extends BaseBean {

    @IntDef({DefinitionCode.SERVER_SUCCESS
            , DefinitionCode.SERVER_UNKNOWN_ERROR
            , DefinitionCode.SERVER_WRONG_DATA_FORMAT
            , DefinitionCode.SERVER_EXPIRED_TOKEN
            , DefinitionCode.SERVER_NO_CHANGE
            , DefinitionCode.SERVER_OUT_OF_DATE_API
            , DefinitionCode.SERVER_OLD_VERSION
            , DefinitionCode.SERVER_CHANGE_BACKEND_SETTING
            , DefinitionCode.SERVER_AGE_DENY
            , DefinitionCode.SERVER_EMAIL_NOT_FOUND
            , DefinitionCode.SERVER_INVALID_EMAIL
            , DefinitionCode.SERVER_EMAIL_REGISTERED
            , DefinitionCode.SERVER_SEND_MAIL_FAIL
            , DefinitionCode.SERVER_INVALID_USER_NAME
            , DefinitionCode.SERVER_INVALID_BIRTHDAY
            , DefinitionCode.SERVER_INCORRECT_PASSWORD
            , DefinitionCode.SERVER_INVALID_PASSWORD
            , DefinitionCode.SERVER_UPLOAD_IMAGE_ERROR
            , DefinitionCode.SERVER_UPLOAD_FILE_ERROR
            , DefinitionCode.SERVER_BUZZ_NOT_FOUND
            , DefinitionCode.SERVER_ACCESS_DENIED
            , DefinitionCode.SERVER_FORBIDDEN_IMAGE
            , DefinitionCode.SERVER_COMMENT_NOT_FOUND
            , DefinitionCode.SERVER_FILE_NOT_FOUND
            , DefinitionCode.SERVER_NOT_IS_APPROVED
            , DefinitionCode.SERVER_LOCKED_FEATURE
            , DefinitionCode.SERVER_BLOCKED_USER
            , DefinitionCode.SERVER_NOT_ENOUGH_MONEY
            , DefinitionCode.SERVER_RECEIVER_NOT_ENOUGH_MONEY
            , DefinitionCode.SERVER_ITEM_NOT_AVAILABLE
            , DefinitionCode.SERVER_USER_NOT_EXIST
            , DefinitionCode.SERVER_LOOKED_USER
            , DefinitionCode.SERVER_INCORRECT_CODE
            , DefinitionCode.SERVER_ALREADY_PURCHASE
            , DefinitionCode.UPDATE_NEWS_FROM_BACKEND
            , DefinitionCode.SERVER_INVALID_TOKEN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DefinitionCode {
        int SERVER_SUCCESS = 0;
        int SERVER_UNKNOWN_ERROR = 1;
        int SERVER_WRONG_DATA_FORMAT = 2;
        int SERVER_EXPIRED_TOKEN = 3;
        int SERVER_NO_CHANGE = 4;
        int SERVER_OUT_OF_DATE_API = 5;
        int SERVER_OLD_VERSION = 6;
        int SERVER_CHANGE_BACKEND_SETTING = 7;
        int SERVER_AGE_DENY = 9;
        int SERVER_EMAIL_NOT_FOUND = 10;
        int SERVER_INVALID_EMAIL = 11;
        int SERVER_EMAIL_REGISTERED = 12;
        int SERVER_SEND_MAIL_FAIL = 13;
        int SERVER_INVALID_USER_NAME = 14;
        int SERVER_INVALID_BIRTHDAY = 15;
        int SERVER_INCORRECT_PASSWORD = 20;
        int SERVER_INVALID_PASSWORD = 21;
        int SERVER_UPLOAD_IMAGE_ERROR = 30;
        int SERVER_UPLOAD_FILE_ERROR = 35;
        int SERVER_BUZZ_NOT_FOUND = 40;
        int SERVER_ACCESS_DENIED = 41;
        int SERVER_FORBIDDEN_IMAGE = 42;
        int SERVER_COMMENT_NOT_FOUND = 43;
        int SERVER_FILE_NOT_FOUND = 46;
        int SERVER_NOT_IS_APPROVED = 47;
        int SERVER_LOCKED_FEATURE = 50;
        int SERVER_BLOCKED_USER = 60;
        int SERVER_NOT_ENOUGH_MONEY = 70;
        int SERVER_RECEIVER_NOT_ENOUGH_MONEY = 71;
        int SERVER_ITEM_NOT_AVAILABLE = 79;
        int SERVER_USER_NOT_EXIST = 80;
        int SERVER_LOOKED_USER = 81;
        int SERVER_INCORRECT_CODE = 90;
        int SERVER_ALREADY_PURCHASE = 99;
        int UPDATE_NEWS_FROM_BACKEND = 96;
        int SERVER_INVALID_TOKEN = 100;//Mandatory login manually (Mandatory login by user),eg: Change password/email from another device or backend
    }

    @DefinitionCode
    @SerializedName("code")
    public int code;

    @SerializedName("new_token")
    public String newToken;

    public int positionItem;

    public ServerResponse() {
    }

    protected ServerResponse(Parcel in) {
        this.code = in.readInt();
        this.newToken = in.readString();
        this.positionItem = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.newToken);
        dest.writeInt(this.positionItem);
    }

}

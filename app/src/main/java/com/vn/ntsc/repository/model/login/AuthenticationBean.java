package com.vn.ntsc.repository.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nankai on 11/29/2016.
 */
public class AuthenticationBean implements Parcelable {

    @SerializedName("token")
    public String token;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("fb_id")
    public String facebookId;
    @SerializedName("original_url")
    public String avartar;
    @SerializedName("gender")
    public int gender;
    @SerializedName("frd_num")
    public int numFriend;
    @SerializedName("fav_num")
    public int numFavourite;
    @SerializedName("fvt_num")
    public int numFavouriteMe;
    @SerializedName("chat_num")
    public int numMyChat;
    @SerializedName("noti_num")
    public int numNotification;
    @SerializedName("noti_news_num")
    public int numNotificationNews;
    @SerializedName("noti_like_num")
    public int numNotificationLike;
    @SerializedName("point")
    public int numPoint;
    @SerializedName("ivt_url")
    public String inviteUrl;
    @SerializedName("finish_register_flag")
    public int finishRegister;
    @SerializedName("verification_flag")
    public int verificationFlag;
    @SerializedName("voice_call_waiting")
    public boolean isEnableVoice;
    @SerializedName("video_call_waiting")
    public boolean isEnableVideo;
    @SerializedName("chk_out_time")
    public int checkoutTime;
    @SerializedName("fav_time")
    public int favouriteTime;
    @SerializedName("bckstg_time")
    public int backstageTime;
    @SerializedName("look_time")
    public String lookAtMeTime;
    @SerializedName("bckstg_pri")
    public int backstagePrice;
    @SerializedName("bckstg_bonus")
    public int backstageBonus;
    @SerializedName("comment_buzz_pnt")
    public int commentPoint;
    @SerializedName("chat_pnt")
    public int chatPoint;
    @SerializedName("day_bns_pnt")
    public int dailyBonusPoints;
    @SerializedName("save_img_pnt")
    public int saveImagePoints;
    @SerializedName("checkout_num")
    public int unlockWhoCheckMeOutPoints;
    @SerializedName("unlck_fvt")
    public int unlockFavoritePoints;
    @SerializedName("onl_alt_pnt")
    public int onlineAlertPoints;
    @SerializedName("wink_bomb_pnt")
    public int winkBombPoints;
    @SerializedName("ivt_frd_pnt")
    public int inviteFriendPoints;
    @SerializedName("rate_distri_point")
    public double rateDistributionPoints;
    @SerializedName("update_email_flag")
    public int isUpdateEmail;
    @SerializedName("turn_off_show_news_android")
    public boolean isShowPopupNews;
    @SerializedName("get_free_point_android")
    public boolean isEnableGetFreePoint;
    @SerializedName("switch_browser_android_version")
    public String switchBrowserVersion;
    @SerializedName("turn_off_user_info_android")
    public boolean isTurnOffUserInfo;
    @SerializedName("bcklst")
    public String blockedUserList;
    @SerializedName("home_page_url")
    public String homePage;
    @SerializedName("email")
    public String email;
    @SerializedName("pwd")
    public String password;
    @SerializedName("bir")
    public String birthDay;
    @SerializedName("id_number")
    public String identifyNumber;
    @SerializedName("phone_number")
    public String phoneNumber;
    @SerializedName("job")
    public int job = -1;
    @SerializedName("region")
    public int region = -1;
    @SerializedName("hobbies")
    public String hobby;
    @SerializedName("body_type")
    public int bodyType = -1;
    @SerializedName("abt")
    public String message; // about me

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.userName);
        dest.writeString(this.userId);
        dest.writeString(this.facebookId);
        dest.writeString(this.avartar);
        dest.writeInt(this.gender);
        dest.writeInt(this.numFriend);
        dest.writeInt(this.numFavourite);
        dest.writeInt(this.numFavouriteMe);
        dest.writeInt(this.numMyChat);
        dest.writeInt(this.numNotification);
        dest.writeInt(this.numNotificationNews);
        dest.writeInt(this.numNotificationLike);
        dest.writeInt(this.numPoint);
        dest.writeString(this.inviteUrl);
        dest.writeInt(this.finishRegister);
        dest.writeInt(this.verificationFlag);
        dest.writeByte(this.isEnableVoice ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isEnableVideo ? (byte) 1 : (byte) 0);
        dest.writeInt(this.checkoutTime);
        dest.writeInt(this.favouriteTime);
        dest.writeInt(this.backstageTime);
        dest.writeString(this.lookAtMeTime);
        dest.writeInt(this.backstagePrice);
        dest.writeInt(this.backstageBonus);
        dest.writeInt(this.commentPoint);
        dest.writeInt(this.chatPoint);
        dest.writeInt(this.dailyBonusPoints);
        dest.writeInt(this.saveImagePoints);
        dest.writeInt(this.unlockWhoCheckMeOutPoints);
        dest.writeInt(this.unlockFavoritePoints);
        dest.writeInt(this.onlineAlertPoints);
        dest.writeInt(this.winkBombPoints);
        dest.writeInt(this.inviteFriendPoints);
        dest.writeDouble(this.rateDistributionPoints);
        dest.writeInt(this.isUpdateEmail);
        dest.writeByte(this.isShowPopupNews ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isEnableGetFreePoint ? (byte) 1 : (byte) 0);
        dest.writeString(this.switchBrowserVersion);
        dest.writeByte(this.isTurnOffUserInfo ? (byte) 1 : (byte) 0);
        dest.writeString(this.blockedUserList);
        dest.writeString(this.homePage);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.birthDay);
        dest.writeString(this.identifyNumber);
        dest.writeString(this.phoneNumber);
        dest.writeInt(this.job);
        dest.writeInt(this.region);
        dest.writeString(this.hobby);
        dest.writeInt(this.bodyType);
        dest.writeString(this.message);
    }

    public AuthenticationBean() {
    }

    protected AuthenticationBean(Parcel in) {
        this.token = in.readString();
        this.userName = in.readString();
        this.userId = in.readString();
        this.facebookId = in.readString();
        this.avartar = in.readString();
        this.gender = in.readInt();
        this.numFriend = in.readInt();
        this.numFavourite = in.readInt();
        this.numFavouriteMe = in.readInt();
        this.numMyChat = in.readInt();
        this.numNotification = in.readInt();
        this.numNotificationNews = in.readInt();
        this.numNotificationLike = in.readInt();
        this.numPoint = in.readInt();
        this.inviteUrl = in.readString();
        this.finishRegister = in.readInt();
        this.verificationFlag = in.readInt();
        this.isEnableVoice = in.readByte() != 0;
        this.isEnableVideo = in.readByte() != 0;
        this.checkoutTime = in.readInt();
        this.favouriteTime = in.readInt();
        this.backstageTime = in.readInt();
        this.lookAtMeTime = in.readString();
        this.backstagePrice = in.readInt();
        this.backstageBonus = in.readInt();
        this.commentPoint = in.readInt();
        this.chatPoint = in.readInt();
        this.dailyBonusPoints = in.readInt();
        this.saveImagePoints = in.readInt();
        this.unlockWhoCheckMeOutPoints = in.readInt();
        this.unlockFavoritePoints = in.readInt();
        this.onlineAlertPoints = in.readInt();
        this.winkBombPoints = in.readInt();
        this.inviteFriendPoints = in.readInt();
        this.rateDistributionPoints = in.readDouble();
        this.isUpdateEmail = in.readInt();
        this.isShowPopupNews = in.readByte() != 0;
        this.isEnableGetFreePoint = in.readByte() != 0;
        this.switchBrowserVersion = in.readString();
        this.isTurnOffUserInfo = in.readByte() != 0;
        this.blockedUserList = in.readString();
        this.homePage = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.birthDay = in.readString();
        this.identifyNumber = in.readString();
        this.phoneNumber = in.readString();
        this.job = in.readInt();
        this.region = in.readInt();
        this.hobby = in.readString();
        this.bodyType = in.readInt();
        this.message = in.readString();
    }

    public static final Creator<AuthenticationBean> CREATOR = new Creator<AuthenticationBean>() {
        @Override
        public AuthenticationBean createFromParcel(Parcel source) {
            return new AuthenticationBean(source);
        }

        @Override
        public AuthenticationBean[] newArray(int size) {
            return new AuthenticationBean[size];
        }
    };
}
package com.vn.ntsc.repository.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

/**
 * Created by nankai on 8/29/2017.
 */

public class UserInfoResponse extends ServerResponse {

    public static final int HEIGHT_ASKME = -1;

    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("original_url")
    public String avatar;
    @SerializedName("is_online")
    public boolean isOnline;
    @SerializedName("long")
    public double longitude;
    @SerializedName("lat")
    public double latitude;
    @SerializedName("dist")
    public double distance;
    @SerializedName("abt")
    public String about;
    @SerializedName("age")
    public int age;
    @SerializedName("gender")
    public int gender;
    @SerializedName("relsh_stt")
    public int relationshipStatus;
    @SerializedName("body_type")
    public int bodyType;
    @SerializedName("inters_in")
    public int interestedIn;
    @SerializedName("lkg_for")
    public int[] lookingFor;

    /**
     * Height=-1 ->ask me
     */
    public double height;
    @SerializedName("ethn")
    public int ethnicity;
    @SerializedName("inters")
    public int[] interests;
    @SerializedName("frd_num")
    public int friendNumber;
    @SerializedName("fvt_num")
    public int favouritedNumber;
    @SerializedName("fav_num")
    public int favouristNumber;
    @SerializedName("point")
    public int point;
    @SerializedName("gift_num")
    public int giftNumber;
    @SerializedName("bckstg_num")
    public int backstageNumber;
    @SerializedName("bckstg_pri")
    public float priceBackstage;
    @SerializedName("pbimg_num")
    public int publicImageNumber;
    @SerializedName("is_fav")
    public int isFavorite;
    @SerializedName("is_frd")
    public boolean isFriend;
    @SerializedName("last_msg")
    public String lastMessage;
    @SerializedName("lst_gift")
    public String[] giftList;
    public String status;
    @SerializedName("is_alt")
    public int isAlt;

    @SerializedName("bir")
    public String birthday;

    @SerializedName("job")
    public int job;

    @SerializedName("region")
    public int region;

    @SerializedName("auto_region")
    public int autoRegion;

    @SerializedName("measurements")
    public int[] threeSizes;

    @SerializedName("cup")
    public int cupSize;

    @SerializedName("cute_type")
    public int cuteType;

    @SerializedName("type_of_man")
    public String typeMan;

    @SerializedName("fetish")
    public String fetish;

    @SerializedName("join_hours")
    public int joinHours;

    @SerializedName("hobby")
    public String hobby;

    @SerializedName("unread_num")
    public int unread_num;

    // ADD_20160801_ITS#21207_HungHN_parse in_call from api.
    @SerializedName("in_call")
    public boolean isCalling;
    // END_20160801_ITS#21207

    @SerializedName("video_call_waiting")
    public boolean isVideoCallWaiting;
    @SerializedName("voice_call_waiting")
    public boolean isVoiceCallWaiting;
    @SerializedName("last_login")
    public String lastLoginTime;
    @SerializedName("update_email_flag")
    public int isUpdateEmail;
    @SerializedName("reviewing_avatar")
    public String reviewingAvatar;

    @SerializedName("noti_num")
    public int notiNum;

    @SerializedName("email")
    public String email;

    @SerializedName("my_footprint_num")
    public int myFootPrintNumber;
    @SerializedName("memo")
    public String memo;
    @SerializedName("thumbnail_url")
    public String thumbnailUrl;
    @SerializedName("countryCode")
    public String countryCode;

    @SerializedName("rate_value")
    public int[] rateValue;


    public int isAlert = 0;

    public UserInfoResponse() {
        super();
    }

    public UserInfoResponse(String userid, String userName) {
        super();
        this.userId = userid;
        this.userName = userName;
    }

    public UserInfoResponse(String userId, int gender, String userName, String avatar, int buzzTypeIsNotFavorite) {
        super();
        this.userId = userId;
        this.gender = gender;
        this.userName = userName;
        this.avatar = avatar;
        this.isFavorite = buzzTypeIsNotFavorite;
    }

    public UserInfoResponse(String userID, int gender, String userName, String avatar) {
        super();
        this.userId = userID;
        this.gender = gender;
        this.userName = userName;
        this.avatar = avatar;
    }

    public void setData(UserInfoResponse response) {
        this.userId = response.userId;
        this.userName = response.userName;
        this.avatar = response.avatar;
        this.isOnline = response.isOnline;
        this.longitude = response.longitude;
        this.latitude = response.latitude;
        this.distance = response.distance;
        this.about = response.about;
        this.age = response.age;
        this.gender = response.gender;
        this.relationshipStatus = response.relationshipStatus;
        this.bodyType = response.bodyType;
        this.interestedIn = response.interestedIn;
        this.lookingFor = response.lookingFor;
        this.height = response.height;
        this.ethnicity = response.ethnicity;
        this.interests = response.interests;
        this.friendNumber = response.friendNumber;
        this.favouritedNumber = response.favouritedNumber;
        this.favouristNumber = response.favouristNumber;
        this.point = response.point;
        this.giftNumber = response.giftNumber;
        this.backstageNumber = response.backstageNumber;
        this.priceBackstage = response.priceBackstage;
        this.publicImageNumber = response.publicImageNumber;
        this.isFavorite = response.isFavorite;
        this.isFriend = response.isFriend;
        this.lastMessage = response.lastMessage;
        this.giftList = response.giftList;
        this.status = response.status;
        this.isAlt = response.isAlt;
        this.birthday = response.birthday;
        this.job = response.job;
        this.region = response.region;
        this.autoRegion = response.autoRegion;
        this.threeSizes = response.threeSizes;
        this.cupSize = response.cupSize;
        this.cuteType = response.cuteType;
        this.typeMan = response.typeMan;
        this.fetish = response.fetish;
        this.joinHours = response.joinHours;
        this.hobby = response.hobby;
        this.unread_num = response.unread_num;
        this.isCalling = response.isCalling;
        this.isVideoCallWaiting = response.isVideoCallWaiting;
        this.isVoiceCallWaiting = response.isVoiceCallWaiting;
        this.lastLoginTime = response.lastLoginTime;
        this.isUpdateEmail = response.isUpdateEmail;
        this.reviewingAvatar = response.reviewingAvatar;
        this.notiNum = response.notiNum;
        this.email = response.email;
        this.myFootPrintNumber = response.myFootPrintNumber;
        this.memo = response.memo;
        this.thumbnailUrl = response.thumbnailUrl;
        this.countryCode = response.countryCode;
        this.rateValue = response.rateValue;
    }

    public void setData(String userId, int gender, String userName, String avatar, int isFavorite) {
        this.userId = userId;
        this.gender = gender;
        this.userName = userName;
        this.avatar = avatar;
        this.isFavorite = isFavorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.avatar);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.distance);
        dest.writeString(this.about);
        dest.writeInt(this.age);
        dest.writeInt(this.gender);
        dest.writeInt(this.relationshipStatus);
        dest.writeInt(this.bodyType);
        dest.writeInt(this.interestedIn);
        dest.writeIntArray(this.lookingFor);
        dest.writeDouble(this.height);
        dest.writeInt(this.ethnicity);
        dest.writeIntArray(this.interests);
        dest.writeInt(this.friendNumber);
        dest.writeInt(this.favouritedNumber);
        dest.writeInt(this.favouristNumber);
        dest.writeInt(this.point);
        dest.writeInt(this.giftNumber);
        dest.writeInt(this.backstageNumber);
        dest.writeFloat(this.priceBackstage);
        dest.writeInt(this.publicImageNumber);
        dest.writeInt(this.isFavorite);
        dest.writeByte(this.isFriend ? (byte) 1 : (byte) 0);
        dest.writeString(this.lastMessage);
        dest.writeStringArray(this.giftList);
        dest.writeString(this.status);
        dest.writeInt(this.isAlt);
        dest.writeString(this.birthday);
        dest.writeInt(this.job);
        dest.writeInt(this.region);
        dest.writeInt(this.autoRegion);
        dest.writeIntArray(this.threeSizes);
        dest.writeInt(this.cupSize);
        dest.writeInt(this.cuteType);
        dest.writeString(this.typeMan);
        dest.writeString(this.fetish);
        dest.writeInt(this.joinHours);
        dest.writeString(this.hobby);
        dest.writeInt(this.unread_num);
        dest.writeByte(this.isCalling ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVideoCallWaiting ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVoiceCallWaiting ? (byte) 1 : (byte) 0);
        dest.writeString(this.lastLoginTime);
        dest.writeInt(this.isUpdateEmail);
        dest.writeString(this.reviewingAvatar);
        dest.writeInt(this.notiNum);
        dest.writeString(this.email);
        dest.writeInt(this.myFootPrintNumber);
        dest.writeString(this.memo);
        dest.writeString(this.thumbnailUrl);
        dest.writeString(this.countryCode);
        dest.writeIntArray(this.rateValue);
    }

    protected UserInfoResponse(Parcel in) {
        super(in);
        this.userId = in.readString();
        this.userName = in.readString();
        this.avatar = in.readString();
        this.isOnline = in.readByte() != 0;
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.distance = in.readDouble();
        this.about = in.readString();
        this.age = in.readInt();
        this.gender = in.readInt();
        this.relationshipStatus = in.readInt();
        this.bodyType = in.readInt();
        this.interestedIn = in.readInt();
        this.lookingFor = in.createIntArray();
        this.height = in.readDouble();
        this.ethnicity = in.readInt();
        this.interests = in.createIntArray();
        this.friendNumber = in.readInt();
        this.favouritedNumber = in.readInt();
        this.favouristNumber = in.readInt();
        this.point = in.readInt();
        this.giftNumber = in.readInt();
        this.backstageNumber = in.readInt();
        this.priceBackstage = in.readFloat();
        this.publicImageNumber = in.readInt();
        this.isFavorite = in.readInt();
        this.isFriend = in.readByte() != 0;
        this.lastMessage = in.readString();
        this.giftList = in.createStringArray();
        this.status = in.readString();
        this.isAlt = in.readInt();
        this.birthday = in.readString();
        this.job = in.readInt();
        this.region = in.readInt();
        this.autoRegion = in.readInt();
        this.threeSizes = in.createIntArray();
        this.cupSize = in.readInt();
        this.cuteType = in.readInt();
        this.typeMan = in.readString();
        this.fetish = in.readString();
        this.joinHours = in.readInt();
        this.hobby = in.readString();
        this.unread_num = in.readInt();
        this.isCalling = in.readByte() != 0;
        this.isVideoCallWaiting = in.readByte() != 0;
        this.isVoiceCallWaiting = in.readByte() != 0;
        this.lastLoginTime = in.readString();
        this.isUpdateEmail = in.readInt();
        this.reviewingAvatar = in.readString();
        this.notiNum = in.readInt();
        this.email = in.readString();
        this.myFootPrintNumber = in.readInt();
        this.memo = in.readString();
        this.thumbnailUrl = in.readString();
        this.countryCode = in.readString();
        this.rateValue = in.createIntArray();
    }

    public static final Parcelable.Creator<UserInfoResponse> CREATOR = new Parcelable.Creator<UserInfoResponse>() {
        @Override
        public UserInfoResponse createFromParcel(Parcel source) {
            return new UserInfoResponse(source);
        }

        @Override
        public UserInfoResponse[] newArray(int size) {
            return new UserInfoResponse[size];
        }
    };
}

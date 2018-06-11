package com.vn.ntsc.repository.model.listpublicimage;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.model.image.Image;
import com.vn.ntsc.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nankai on 8/29/2017.
 */

public class ProfilePictureData extends BaseBean{

    public static final String USER_ID = "user_id";
    public static final String AVATA = "avata";
    public static final String START_LOCATION = "start_location";
    public static final String NUMBER_OF_IMAGE = "number_of_image";
    public static final String GENDER_PROFILE = "gender_profile";
    public static final String BUZZ_ID = "buzz_id";
    public static final String CURENT_IMG_ID = "current_img_id";
    public static final String IMAGE_CACHE_DIR = "thumbs";
    public static final int REQUEST_CODE_GET_IMAGE = 0;
    public static final int LOADER_UPDATE_AVATAR = 1;
    public static final String DATA_TYPE = "data_type";
    public static final int TYPE_UNKNOW = 0;
    public static final int TYPE_BUZZ = 1;
    public static final int TYPE_PROFILE = 2;
    public static final int TYPE_PROFILE_AVATA = 3;
    public static final int TYPE_GALLERY = 4;
    public static final int TYPE_SAVE_CHAT = 5;
    public static final int TYPE_BACKSTAGE = 6;
    public static final int TYPE_BACKSTAGE_APPROVE = 7;
    public static final int TYPE_PREVIOUS_PHOTO = 8;

    // Data of class
    public int mDataType = TYPE_UNKNOW;
    public String mUserId = "";
    public String mAvata = "";
    public String mImgId = "";
    public int mStartLocation = 0;
    public int mNumberOfImage = 0;
    public List<Image> mListImage = new ArrayList<Image>();
    public int mGender = Constants.GENDER_TYPE_MAN;

    public void addListImg(List<Image> listImage) {
        for (Image img : listImage) {
            if (!this.mListImage.contains(img)) {
                this.mListImage.add(img);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDataType);
        dest.writeString(this.mUserId);
        dest.writeString(this.mAvata);
        dest.writeString(this.mImgId);
        dest.writeInt(this.mStartLocation);
        dest.writeInt(this.mNumberOfImage);
        dest.writeTypedList(this.mListImage);
        dest.writeInt(this.mGender);
    }

    public ProfilePictureData() {
    }

    protected ProfilePictureData(Parcel in) {
        this.mDataType = in.readInt();
        this.mUserId = in.readString();
        this.mAvata = in.readString();
        this.mImgId = in.readString();
        this.mStartLocation = in.readInt();
        this.mNumberOfImage = in.readInt();
        this.mListImage = in.createTypedArrayList(Image.CREATOR);
        this.mGender = in.readInt();
    }

    public static final Parcelable.Creator<ProfilePictureData> CREATOR = new Parcelable.Creator<ProfilePictureData>() {
        @Override
        public ProfilePictureData createFromParcel(Parcel source) {
            return new ProfilePictureData(source);
        }

        @Override
        public ProfilePictureData[] newArray(int size) {
            return new ProfilePictureData[size];
        }
    };
}

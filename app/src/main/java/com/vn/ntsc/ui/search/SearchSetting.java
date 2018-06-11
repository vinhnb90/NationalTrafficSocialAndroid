package com.vn.ntsc.ui.search;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.repository.json.boby.BodyType;
import com.vn.ntsc.repository.json.boby.BodyTypeItem;
import com.vn.ntsc.repository.json.gender.GenderType;
import com.vn.ntsc.repository.json.gender.SearchGender;
import com.vn.ntsc.repository.json.regions.RegionItem;
import com.vn.ntsc.repository.json.regions.Regions;
import com.vn.ntsc.repository.json.searchavatar.Avatar;
import com.vn.ntsc.repository.json.searchavatar.SearchAvatar;
import com.vn.ntsc.repository.json.sortorder.Order;
import com.vn.ntsc.repository.json.sortorder.SearchSortOrder;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.AssetsUtils;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.UserSetting;

import java.util.List;

/**
 * Created by hnc on 18/08/2017.
 */

public class SearchSetting implements Parcelable {

    private int mMinAge;
    private int mMaxAge;

    private boolean mLoginWithin24h;
    private boolean mNoInteracted;

    public List<Avatar> mSearchAvatar;
    public List<Order> mSearchSortOrder;
    public List<BodyTypeItem> mBodyMale;
    public List<BodyTypeItem> mBodyFemale;
    public List<RegionItem> mAllRegions;
    public List<GenderType> mGender;

    private BodyTypeItem mCurrentBodyType;
    private Avatar mCurrentAvatar;
    private Order mCurrentOrderSort;
    private GenderType mCurrentGender;
    private List<RegionItem> mSelectedRegion;


    public SearchSetting(Context context) {

        mSearchSortOrder = AssetsUtils.getDataAssets(context, Constants.PATH_SORT_ODER, SearchSortOrder.class).mData;
        mSearchAvatar = AssetsUtils.getDataAssets(context, Constants.PATH_AVATAR, SearchAvatar.class).mData;
        mAllRegions = AssetsUtils.getDataAssets(context, Constants.PATH_REGIONS, Regions.class).regions;
        mBodyMale = AssetsUtils.getDataAssets(context, Constants.PATH_BODY_TYPE, BodyType.class).bodyTypeMale;
        mBodyFemale = AssetsUtils.getDataAssets(context, Constants.PATH_BODY_TYPE, BodyType.class).bodyTypeFeMale;
        mGender = AssetsUtils.getDataAssets(context, Constants.PATH_GENDER, SearchGender.class).mDataGender;
    }

    public List<Order> getListOrder() {
        return mSearchSortOrder;
    }

    public List<Avatar> getListAvatar() {
        return mSearchAvatar;
    }

    public List<GenderType> getListGender() {
        return mGender;
    }

    public List<BodyTypeItem> getArrayBodyMale() {
        return mBodyMale;
    }

    public List<BodyTypeItem> getArrayBodyFemale() {
        return mBodyFemale;
    }

    public List<RegionItem> getAllRegions() {
        return mAllRegions;
    }

    public List<RegionItem> getSelectedRegions() {
        if (mSelectedRegion == null || mSelectedRegion.isEmpty()) {
            return mAllRegions;
        }
        return mSelectedRegion;
    }

    public void setSelectedRegion(List<RegionItem> selectedRegion) {
        this.mSelectedRegion = selectedRegion;
    }

    public int getMinAge() {
        return mMinAge;
    }

    public void setMinAge(int minAge) {
        mMinAge = minAge;
    }

    public int getMaxAge() {
        return mMaxAge;
    }

    public void setMaxAge(int maxAge) {
        mMaxAge = maxAge;
    }

    public BodyTypeItem getCurrentBodyType() {
        if (mCurrentBodyType == null) {
            return (UserPreferences.getInstance().getGender() == UserSetting.GENDER_MALE) ?
                    getArrayBodyMale().get(0) : getArrayBodyFemale().get(0);
        }
        LogUtils.d("getCurrentBodyType ", " " + mCurrentBodyType.name);

        return mCurrentBodyType;
    }

    public void setCurrentGender(GenderType currentGender) {
        mCurrentGender = currentGender;
    }


    public void setCurrentBodyType(BodyTypeItem currentBodyType) {
        this.mCurrentBodyType = currentBodyType;
    }

    public GenderType getCurrentGender() {
        if (mCurrentGender == null) {
            return getListGender().get(0);
        }
        LogUtils.d("getCurrentGender ", " " + mCurrentGender.value);
        return mCurrentGender;
    }

    public Avatar getCurrentAvatar() {
        if (mCurrentAvatar == null) {
            return getListAvatar().get(0);
        }
        return mCurrentAvatar;
    }

    public void setCurrentAvatar(Avatar avatar) {
        this.mCurrentAvatar = avatar;
    }

    public Order getCurrentOrderSort() {
        if (mCurrentOrderSort == null) {
            return getListOrder().get(0);
        }
        return mCurrentOrderSort;
    }


    public void setCurrentOrderSort(Order sort) {
        this.mCurrentOrderSort = sort;
    }


    public boolean isLoginWithin24h() {
        return mLoginWithin24h;
    }

    public void setLoginWithin24h(boolean loginWithin24h) {
        mLoginWithin24h = loginWithin24h;
    }

    public boolean isNoInteracted() {
        return mNoInteracted;
    }

    public void setNoInteracted(boolean noInteracted) {
        mNoInteracted = noInteracted;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMinAge);
        dest.writeInt(this.mMaxAge);
        dest.writeByte(this.mLoginWithin24h ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mNoInteracted ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.mSearchAvatar);
        dest.writeTypedList(this.mSearchSortOrder);
        dest.writeTypedList(this.mBodyMale);
        dest.writeTypedList(this.mBodyFemale);
        dest.writeTypedList(this.mAllRegions);
        dest.writeParcelable(this.mCurrentBodyType, flags);
        dest.writeParcelable(this.mCurrentAvatar, flags);
        dest.writeParcelable(this.mCurrentOrderSort, flags);
        dest.writeTypedList(this.mSelectedRegion);
    }

    protected SearchSetting(Parcel in) {
        this.mMinAge = in.readInt();
        this.mMaxAge = in.readInt();
        this.mLoginWithin24h = in.readByte() != 0;
        this.mNoInteracted = in.readByte() != 0;
        this.mSearchAvatar = in.createTypedArrayList(Avatar.CREATOR);
        this.mSearchSortOrder = in.createTypedArrayList(Order.CREATOR);
        this.mBodyMale = in.createTypedArrayList(BodyTypeItem.CREATOR);
        this.mBodyFemale = in.createTypedArrayList(BodyTypeItem.CREATOR);
        this.mAllRegions = in.createTypedArrayList(RegionItem.CREATOR);
        this.mCurrentBodyType = in.readParcelable(BodyTypeItem.class.getClassLoader());
        this.mCurrentAvatar = in.readParcelable(Avatar.class.getClassLoader());
        this.mCurrentOrderSort = in.readParcelable(Order.class.getClassLoader());
        this.mSelectedRegion = in.createTypedArrayList(RegionItem.CREATOR);
    }

    public static final Creator<SearchSetting> CREATOR = new Creator<SearchSetting>() {
        @Override
        public SearchSetting createFromParcel(Parcel source) {
            return new SearchSetting(source);
        }

        @Override
        public SearchSetting[] newArray(int size) {
            return new SearchSetting[size];
        }
    };
}

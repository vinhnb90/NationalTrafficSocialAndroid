package com.vn.ntsc.repository.model.region;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.repository.TypeView;

/**
 * Created by nankai on 8/8/2017.
 */

public class Region extends RegionComponent {
    public static final int REGION_NOT_SET = -1;
    public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() {
        @Override
        public Region createFromParcel(Parcel source) {
            return new Region(source);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };
    private boolean isSelected = false;
    private int code;
    private String alias;
    private String name;

    public Region(int code, String alias, String name) {
        super();
        this.code = code;
        this.alias = alias;
        this.name = name;
    }

    public Region() {
    }

    protected Region(Parcel in) {
        this.isSelected = in.readByte() != 0;
        this.code = in.readInt();
        this.alias = in.readString();
        this.name = in.readString();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int getType() {
        return TypeView.RegionTypeView.REGION;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Region other = (Region) obj;
        return code == other.code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.code);
        dest.writeString(this.alias);
        dest.writeString(this.name);
    }
}

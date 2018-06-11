package com.vn.ntsc.repository.model.region;

import android.os.Parcel;
import android.os.Parcelable;

import com.vn.ntsc.repository.TypeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nankai on 8/8/2017.
 */

public class RegionGroup extends RegionComponent {
    public static final Parcelable.Creator<RegionGroup> CREATOR = new Parcelable.Creator<RegionGroup>() {
        @Override
        public RegionGroup createFromParcel(Parcel source) {
            return new RegionGroup(source);
        }

        @Override
        public RegionGroup[] newArray(int size) {
            return new RegionGroup[size];
        }
    };
    private final int CODE_REGION_GROUP = -2;
    private List<Region> region = new ArrayList<>();
    private String name;

    public RegionGroup() {
    }

    public RegionGroup(List<Region> region, String name) {
        super();
        this.region = region;
        this.name = name;
    }

    protected RegionGroup(Parcel in) {
        this.region = in.createTypedArrayList(Region.CREATOR);
        this.name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Region> getRegion() {
        return region;
    }

    public void setRegion(List<Region> region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "RegionGroup [region=" + region + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
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
        RegionGroup other = (RegionGroup) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        return true;
    }

    @Override
    public boolean isSelected() {
        boolean isSelected = true;
        for (RegionComponent regionLeaf : region) {
            if (!regionLeaf.isSelected()) {
                isSelected = false;
                break;
            }
        }
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        for (RegionComponent regionComponent : region) {
            regionComponent.setSelected(true);
        }
    }

    @Override
    public int getType() {
        return TypeView.RegionTypeView.REGION_GROUP;
    }

    @Override
    public int getCode() {
        return CODE_REGION_GROUP;
    }

    @Override
    public void setCode(int code) {

    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public void setAlias(String alias) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CODE_REGION_GROUP);
        dest.writeTypedList(this.region);
        dest.writeString(this.name);
    }
}

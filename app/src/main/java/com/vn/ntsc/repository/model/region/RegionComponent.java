package com.vn.ntsc.repository.model.region;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.repository.TypeView;

/**
 * Created by nankai on 8/8/2017.
 */

public abstract class RegionComponent extends BaseBean {
    public abstract boolean isSelected();

    public abstract void setSelected(boolean isSelected);

    @TypeView.RegionTypeView
    public abstract int getType();

    public abstract int getCode();

    public abstract void setCode(int code);

    public abstract String getAlias();

    public abstract void setAlias(String alias);

    public abstract String getName();

    public abstract void setName(String name);


}

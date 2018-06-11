package com.vn.ntsc.ui.main;

import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.MenuLeftBean;

/**
 * Created by nankai on 8/25/2017.
 */

public interface MenuLeftListener {
    void openItemMenuLeft(MenuLeftBean bean, @TypeView.MenuLeft int menuId);
}

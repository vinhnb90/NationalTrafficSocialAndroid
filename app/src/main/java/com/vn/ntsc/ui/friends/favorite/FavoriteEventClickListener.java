package com.vn.ntsc.ui.friends.favorite;

import android.view.View;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * Created by hnc on 10/08/2017.
 */

public interface FavoriteEventClickListener<E extends BaseBean> extends BaseAdapterListener<E> {
    void onItemClick(E data, View view, int position);

    void onMenuItemClick(E data, View view, int position);
}

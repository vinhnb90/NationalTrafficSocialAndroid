package com.vn.ntsc.ui.tagfriends;

import android.view.View;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * Created by Robert on 2017 Sep 14.
 */
public interface TagFriendEventListener<E extends BaseBean> extends BaseAdapterListener<E> {
    void onItemClick(E data, View view, int position);
}

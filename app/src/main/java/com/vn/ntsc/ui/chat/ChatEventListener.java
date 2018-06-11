package com.vn.ntsc.ui.chat;

import android.view.View;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * The Post status interface handle all event when user Tap into view
 * @author Created by Robert on 2017 Sep 05
 */
public interface ChatEventListener<E extends BaseBean> extends BaseAdapterListener<E> {

    /**
     * Call when user tap to Image/Video item to view fullscreen
     * @param data the BaseBean of Entity object
     * @param view
     * @param position
     */
    void onItemClick(E data, View view, int position);

}

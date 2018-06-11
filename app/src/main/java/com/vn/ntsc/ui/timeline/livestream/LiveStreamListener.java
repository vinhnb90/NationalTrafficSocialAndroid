package com.vn.ntsc.ui.timeline.livestream;

import android.view.View;

import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * Created by nankai on 8/10/2017.
 */

public interface LiveStreamListener<E> extends BaseAdapterListener {

    void onItemLiveStreamListener(E item, int position, View view);
}

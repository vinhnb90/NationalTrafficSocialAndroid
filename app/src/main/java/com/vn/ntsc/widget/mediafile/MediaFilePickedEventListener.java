package com.vn.ntsc.widget.mediafile;

import android.view.View;

import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

/**
 * The MediaFileSelected to upload interface handle all event when user Tap into view
 * @author Created by Robert on 2017 Sep 11
 */
public interface MediaFilePickedEventListener<E extends BaseBean> extends BaseAdapterListener<E> {
    /**
     * Call when user tap to Remove Button to Select/Deselect Image/Video
     * @param data the BaseBean of Entity object
     * @param view
     * @param position
     */
    void onUnSelectFile(E data, View view, int position);

}

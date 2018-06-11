package com.vn.ntsc.ui.profile.media.createAlbum;

import com.vn.ntsc.core.callback.CallbackListener;

/**
 * Created by ThoNh on 11/14/2017.
 */

public interface CreateAlbumContract {
    interface View extends CallbackListener {

        void initSelectedRecyclerView();

        void initChooseRecyclerView();

    }
}

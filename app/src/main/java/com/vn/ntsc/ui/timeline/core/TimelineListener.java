package com.vn.ntsc.ui.timeline.core;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.widget.adapter.BaseAdapterListener;

import java.util.ArrayList;

/**
 * Created by nankai on 8/8/2017.
 */

public interface TimelineListener<E> extends BaseAdapterListener {

    //Normal
    void onDisplayImageDetailScreen(E bean, int positionTimeLineAdapter, int positionIndexItem, View view);

    void onDisplayLiveStreamScreen(E bean, int positionTimeLineAdapter, int positionIndexItem, View view);

    void onDisplayCommentScreen(E bean, int position, View view);

    void onLike(E bean, int position, View view);

    void onShare(E bean, int position, View view);

    void onFavorite(E bean, int position, View view);

    void onRemoveStatus(E bean, int position, View view);

    void onDisplayProfileScreen(E bean, int position, View view);

    void onDisplayProfileScreen(String userId, int position, View view);

    void onDisplayTagFriendsScreen(ArrayList<ListTagFriendsBean> listTagFriendsBeans, int position, View view);

    void onApproval(E bean, int position, View view);

    //Share
    void onDisplayShareAudioPlayScreen(E bean, int position, View view);

    void onDisplayShareLiveStreamScreen(E bean, int position, View view);

    //Template
    void onRemoveStatusTemplate(E bean, int position, View view);

    void onRetryBuzzDetailRequest(BuzzDetailRequest request, String templateId, int position, View view);
}
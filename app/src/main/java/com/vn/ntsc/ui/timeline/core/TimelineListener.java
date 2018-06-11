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
    void onItemTimelineClick(E item, int position, View view);

    void onShowImageDetail(E bean, int positionTimeLineAdapter, int positionIndexItem, View view);

    void onShowLiveStream(E bean, int positionTimeLineAdapter, int positionIndexItem, View view);

    void onLike(E bean, int position, View view);

    void onShowComment(E bean, int position, View view);

    void onShare(E bean, int position, View view);

    void onDeleteStatus(E bean, int position, View view);

    void onRemoveStatusTemplate(E bean, int position, View view);

    void onRetryBuzzDetailRequest(BuzzDetailRequest request, String templateId, int position, View view);

    void onFavorite(E bean, int position, View view);

    void onShowProfile(E bean, int position, View view);

    void onShowProfile(String userId, int position, View view);

    void onShowTagFriendsDetail(ArrayList<ListTagFriendsBean> listTagFriendsBeans, int position, View view);

    void onApproval(E bean, int position, View view);

    void onPlayAudioShare(E bean, int position, View view);

    void onPlayLiveStreamShare(E bean, int position, View view);
}
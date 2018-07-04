package com.vn.ntsc.ui.comments;

import android.view.View;

import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;

import java.util.ArrayList;

public interface HeaderCommentListener {

    //Normal
    void onDisplayImageDetailScreen(BuzzBean bean, int positionIndexItem, View view);

    void onDisplayLiveStreamScreen(BuzzBean bean, int positionIndexItem, View view);

    void onFavorite(BuzzBean bean, View view);

    void onRemoveStatus(BuzzBean bean, View view);

    void onDisplayProfileScreen(BuzzBean bean, View view);

    void onDisplayProfileScreen(String userId, View view);

    void onDisplayTagFriendsScreen(ArrayList<ListTagFriendsBean> listTagFriendsBeans, View view);

    void onApproval(BuzzBean bean, View view);

    //Share
    void onDisplayShareAudioPlayScreen(BuzzBean bean, View view);

    void onDisplayShareLiveStreamScreen(BuzzBean bean, View view);

}

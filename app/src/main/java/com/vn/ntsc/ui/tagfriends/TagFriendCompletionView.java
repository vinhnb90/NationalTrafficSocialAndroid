package com.vn.ntsc.ui.tagfriends;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.views.tokenautocomplete.TokenCompleteTextView;
import com.vn.ntsc.widget.views.tokenautocomplete.TokenTextView;

/**
 * Tag friend completion view for basic friend info
 *
 * @author Created by Robert on 2017 Sep 14
 */
public class TagFriendCompletionView extends TokenCompleteTextView<ListTagFriendsBean> {

    public TagFriendCompletionView(Context context) {
        super(context);
    }

    public TagFriendCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagFriendCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(final ListTagFriendsBean friend) {

        LayoutInflater mLayoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View tokenView = mLayoutInflater.inflate(R.layout.item_autocomplete_tag_friend, (ViewGroup) getParent(), false);
        final TokenTextView textView = tokenView.findViewById(R.id.item_autocomplete_tag_friend_token_text);
        final ImageView avatar = tokenView.findViewById(R.id.item_autocomplete_tag_friend_icon);
        textView.setText(friend.userName);
        textView.setSelected(true);
        ImagesUtils.loadRoundedAvatar(friend.avatar,friend.gender,avatar);
        return tokenView;
    }

    @Override
    public ListTagFriendsBean defaultObject(String completionText) {
        ListTagFriendsBean friendItem = new ListTagFriendsBean();
        friendItem.userName = "";
        return friendItem;
    }
}

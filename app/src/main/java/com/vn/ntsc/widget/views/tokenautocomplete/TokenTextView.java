package com.vn.ntsc.widget.views.tokenautocomplete;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.vn.ntsc.R;

/**
 * Created by mgod on 5/27/15.
 *
 * Simple custom view example to show how to get selected events from the token
 * view. See TagFriendCompletionView and item_list_tag_friends_favorited.xml for usage
 */
public class TokenTextView extends AppCompatTextView {

    public TokenTextView(Context context) {
        super(context);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_white_18dp, 0);
    }

    public TokenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_white_18dp, 0);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        //setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.drawable.ic_clear_white_18dp : 0, 0);
    }
}

package com.vn.ntsc.widget.views.popup.mymore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.Utils;

/**
 * Created by nankai on 9/13/2017.
 */

public class MyMoreLayout extends LinearLayout implements View.OnClickListener {

    private OnMoreListener listener;
    private UserInfoResponse mUserInfoResponse;
    private PopupWindow popup;
    private LinearLayout onlineAlert;
    private LinearLayout report;
    private LinearLayout block;
    private LinearLayout favorite;
    private ImageView imgfavorite;
    private LinearLayout sendGift;
    private View view;

    public MyMoreLayout(Context context, OnMoreListener listener, UserInfoResponse userProfileBean, @TypeView.MyMoreLayout int type) {
        super(context);
        init(context, listener, userProfileBean);
    }


    public MyMoreLayout(Context context, OnMoreListener listener, UserInfoResponse userProfileBean) {
        super(context);
        init(context, listener, userProfileBean);

    }

    private void init(Context context, OnMoreListener listener, UserInfoResponse userProfileBean) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_profile_more_options, this, true);
        this.listener = listener;
        this.mUserInfoResponse = userProfileBean;

        onlineAlert = view.findViewById(R.id.menu_more_online_alert);
        report = view.findViewById(R.id.menu_more_report);
        block = view.findViewById(R.id.menu_more_interested);
        favorite = view.findViewById(R.id.menu_more_favorite);
        sendGift = view.findViewById(R.id.menu_more_gift);
        imgfavorite = view.findViewById(R.id.favorite);

        setFavorite(userProfileBean.isFavorite);

        onlineAlert.setOnClickListener(this);
        report.setOnClickListener(this);
        block.setOnClickListener(this);
        favorite.setOnClickListener(this);
        sendGift.setOnClickListener(this);
    }


    public View getViewOnline() {
        return onlineAlert;
    }

    /**
     * update view and state of favourite
     *
     * @param isFavorite BUZZ_TYPE_IS_FAVORITE or BUZZ_TYPE_IS_NOT_FAVORITE
     */
    public void setFavorite(int isFavorite) {
        // update ui
        if (isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE)
            imgfavorite.setImageResource(R.drawable.ic_list_buzz_item_favorited);
        else
            imgfavorite.setImageResource(R.drawable.ic_submenu_favorite_outline);

        // set data favourite
        mUserInfoResponse.isFavorite = isFavorite;
    }

    @Override
    public void onClick(View view) {
        if (Utils.isEmptyOrNull(UserPreferences.getInstance().getToken())) {
            listener.onWarningNotLogin();
            return;
        }
        switch (view.getId()) {
            case R.id.menu_more_online_alert:
                listener.onAlertOnline(mUserInfoResponse);
                break;
            case R.id.menu_more_report:
                listener.onReport(mUserInfoResponse);
                break;
            case R.id.menu_more_interested:
                listener.onBlock(mUserInfoResponse);
                break;
            case R.id.menu_more_favorite:
                listener.onFavorite(mUserInfoResponse);
                break;
            case R.id.menu_more_gift:
                listener.onSendGift(mUserInfoResponse);
                break;
        }
    }
}

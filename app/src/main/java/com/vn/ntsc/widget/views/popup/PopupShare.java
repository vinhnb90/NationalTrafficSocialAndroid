package com.vn.ntsc.widget.views.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.vn.ntsc.R;

/**
 * Created by nankai on 8/9/2017.
 */

public class PopupShare {
    public static final int TYPE_SHARE_WALL = 1;
    public static final int TYPE_SHARE_FACEBOOK = 2;
    private OnPopupShareListener popupListener;

    public PopupShare(OnPopupShareListener popupListener) {
        this.popupListener = popupListener;
    }

    public void show(Context context, View view, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.layout_popup_share, null);

        TextView shareWall = (TextView) popupView.findViewById(R.id.layout_popup_share_wall);
        TextView shareFacebook = (TextView) popupView.findViewById(R.id.layout_popup_share_facebook);

        PopupWindow popup = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popup.setAnimationStyle(R.style.animationName);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.setOutsideTouchable(true);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popup.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - (view.getHeight() * 5));
        popup.update();

        PopupListener popupListener = new PopupListener(position, popup);
        shareWall.setOnClickListener(popupListener);
        shareFacebook.setOnClickListener(popupListener);
    }

    private class PopupListener implements View.OnClickListener {
        private int position;
        private PopupWindow popup;

        public PopupListener(int position, PopupWindow popup) {
            this.position = position;
            this.popup = popup;
        }

        @Override
        public void onClick(View view) {
            int type = -1;
            switch (view.getId()) {
                case R.id.layout_popup_share_wall:
                    type = TYPE_SHARE_WALL;
                    break;
                case R.id.layout_popup_share_facebook:
                    type = TYPE_SHARE_FACEBOOK;
                    break;
            }
            popupListener.onChoiceShareSuccess(position, type);
            popup.dismiss();
        }
    }
}

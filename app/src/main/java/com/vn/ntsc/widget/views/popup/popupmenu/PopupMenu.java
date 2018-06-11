package com.vn.ntsc.widget.views.popup.popupmenu;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

/**
 * Created by hnc on 10/08/2017.
 */

public class PopupMenu {
    public static void show(Context context, View view, int menuRes) {
        android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(context, view);
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        ((View) view.getParent()).setAlpha(0.2f);
    }
}

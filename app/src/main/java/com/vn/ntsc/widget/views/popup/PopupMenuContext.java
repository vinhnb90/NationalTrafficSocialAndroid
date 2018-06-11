package com.vn.ntsc.widget.views.popup;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

/**
 * Created by ThoNH on 14/08/2017.
 */

// Don't use android.widget.v7.PopupMenu --> bug when show popup inside RecyclerView
// https://stackoverflow.com/questions/29473977/popupmenu-click-causing-recyclerview-to-scroll

public class PopupMenuContext {
    private PopupBuilder mPopupBuilder;

    public PopupMenuContext(PopupBuilder popupBuilder) {
        mPopupBuilder = popupBuilder;
        show();
    }

    public void dissmiss() {
        mPopupBuilder.getPopupMenu().dismiss();
    }

    public void show() {
        mPopupBuilder.getPopupMenu().show();
    }

    public Menu getMenu() {
        return mPopupBuilder.getMenu();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public int getGravity() {
        return mPopupBuilder.getGravity();
    }

    public static class PopupBuilder {

        private PopupMenu mPopupMenu;

        /**
         * Create Popup menu
         *
         * @param context      context
         * @param menuView     khi bấm vào view này thì hiện menu
         * @param menuResource menu resource trong R.menu
         */
        public PopupBuilder(Context context, View menuView, int menuResource) {
            mPopupMenu = new PopupMenu(context, menuView);
            mPopupMenu.getMenuInflater().inflate(menuResource, mPopupMenu.getMenu());
        }

        /**
         * Set gravity cho menu
         *
         * @param gravity {@link Gravity#}
         * @return PopupBuilder
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        public PopupBuilder setGravity(int gravity) {
            mPopupMenu.setGravity(gravity);
            return this;
        }

        /**
         * Lắng nghe sự kiện item của popup menu được click
         *
         * @param listener {@link PopupMenu.OnMenuItemClickListener}
         * @return PopupBuilder
         */
        public PopupBuilder setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener listener) {
            mPopupMenu.setOnMenuItemClickListener(listener);
            return this;
        }

        /**
         * Set listener dissmiss cho popup
         * Gọi khi popup bị dissmiss
         *
         * @param dissmissListener {@link PopupMenu.OnDismissListener}
         * @return PopupBuilder
         */
        public PopupBuilder setOnDissmissListener(PopupMenu.OnDismissListener dissmissListener) {
            mPopupMenu.setOnDismissListener(dissmissListener);
            return this;
        }

        public PopupMenuContext build() {
            return new PopupMenuContext(this);
        }

        /**
         * @return Gravity hiện tại của popupMenu {@link Gravity}
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        public int getGravity() {
            return mPopupMenu.getGravity();
        }

        /**
         * @return Menu hiện tại của popupMenu
         */
        public Menu getMenu() {
            return mPopupMenu.getMenu();
        }

        /**
         * @return popupMenu
         */
        public PopupMenu getPopupMenu() {
            return mPopupMenu;
        }

    }


}

package com.nankai.designlayout.tabs.buider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nankai.designlayout.tabs.TabAdapter;

/**
 * Created by nankai on 11/16/2016.
 */

public class Header {

    public TabAdapter tabAdapter;
    public TabLayout tabLayout;
    private Context context;
    private Builder builder;

    /**
     * @param builder
     * @param context
     * @param tabLayout
     * @param tabAdapter
     */
    public Header(Builder builder, Context context, TabLayout tabLayout, TabAdapter tabAdapter) {
        this.tabAdapter = tabAdapter;
        this.tabLayout = tabLayout;
        this.context = context;
        this.builder = builder;
    }

    /**
     * <b>Set title</b>
     *
     * @param title type String[]
     */
    public Builder setTitle(String... title) {
        if (tabAdapter.geLength() <= 0)
            return builder;

        if (title.length < tabAdapter.geLength()) {
            Log.e("tabView", "title less tab");
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                tabLayout.getTabAt(i).setText("Tab " + i);
            }
        } else {
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                tabLayout.getTabAt(i).setText(title[i]);
            }
        }
        return builder;
    }

    /**
     * <b>Set title</b>
     *
     * @param titleIds type StringRes int[]
     */
    public Builder setTitle(@StringRes int... titleIds) {
        if (tabAdapter.geLength() <= 0)
            return builder;

        if (titleIds.length < tabAdapter.geLength()) {
            Log.e("tabView", "title less tab");
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                tabLayout.getTabAt(i).setText("Tab " + i);
            }
        } else {
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                if (titleIds[i] == 0) {
                    tabLayout.getTabAt(i).setText("");
                } else {
                    tabLayout.getTabAt(i).setText(context.getResources().getString(titleIds[i]));
                }
            }
        }
        return builder;
    }

    /**
     * <b>Set Icon</b>
     *
     * @param iconIds type DrawableRes int[]
     */
    public Builder setIcon(@DrawableRes int... iconIds) {
        if (tabAdapter.geLength() <= 0)
            return builder;

        if (iconIds.length < tabAdapter.geLength()) {
            Log.e("tabView", "title less tab");
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                tabLayout.getTabAt(i).setIcon(null);
            }
        } else {
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                if (iconIds[i] == 0) {
                    Log.e("tabView", "title less tab");
                    tabLayout.getTabAt(i).setIcon(null);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tabLayout.getTabAt(i).setIcon(context.getResources().getDrawable(iconIds[i], context.getTheme()));
                    } else {
                        tabLayout.getTabAt(i).setIcon(context.getResources().getDrawable(iconIds[i]));
                    }
                }
            }
        }
        return builder;
    }

    /**
     * <b>Set title</b>
     *
     * @param drawable type Drawable[]
     */
    public Builder setIcon(Drawable... drawable) {
        if (tabAdapter.geLength() <= 0)
            return builder;

        if (drawable.length < tabAdapter.geLength()) {
            Log.e("tabView", "title less tab");
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                tabLayout.getTabAt(i).setIcon(null);
            }
        } else {
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                tabLayout.getTabAt(i).setIcon(drawable[i]);
            }
        }
        return builder;
    }

    /**
     * @param layout
     * @param IDIcon
     * @param IDtitle
     * @param iconIds
     * @param titleIds
     * @return
     */
    public Builder addCustomLayout(@LayoutRes int layout, @IdRes int IDIcon, @IdRes int IDtitle, @DrawableRes int[] iconIds, @StringRes int[] titleIds) {
        if (tabAdapter.geLength() <= 0)
            return builder;
        if (iconIds.length < tabAdapter.geLength() && titleIds.length < tabAdapter.geLength()) {
            Log.e("tabView", "title less tab");
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                tabLayout.getTabAt(i).setIcon(null);
            }
        } else {
            for (int i = 0; i < tabAdapter.geLength(); i++) {
                View view = LayoutInflater.from(context).inflate(layout, null);
                TextView title = (TextView) view.findViewById(IDtitle);
                ImageView icon = (ImageView) view.findViewById(IDIcon);
                icon.setImageResource(iconIds[i]);
                title.setText(titleIds[i]);
                tabLayout.getTabAt(i).setCustomView(view);
            }
        }
        return builder;
    }
}

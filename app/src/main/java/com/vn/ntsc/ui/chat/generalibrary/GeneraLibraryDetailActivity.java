package com.vn.ntsc.ui.chat.generalibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.ui.mediadetail.base.BaseMediaActivity;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;

/**
 * Created by Doremon on 3/1/2018.
 */

public class GeneraLibraryDetailActivity extends BaseMediaActivity {

    @BindView(R.id.activity_genera_lib_detail_scrim_view)
    View scrimView;

    @BindView(R.id.activity_genera_lib_detail_image_pager)
    ViewPager mViewPager;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_genera_lib_detail_container)
    RelativeLayout container;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, GeneraLibraryDetailActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void orientationChange(int orient) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_genera_lib_detail;
    }

    @Override
    public void onCreateView(View rootView) {
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}

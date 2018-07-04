package com.vn.ntsc.ui.profile.media.edit.description.album;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindView;

/**
 * Created by ThoNh on 2/9/2018.
 */

public class EditAlbumDescriptionActivity extends BaseActivity implements ToolbarButtonRightClickListener {

    public static final String VIEW_SHARE_ELEMENT = "VIEW_SHARE_ELEMENT";
    public static final String EXTRA_IS_OWN = "EXTRA_IS_OWN";
    public static final String EXTRA_JUST_VIEW = "EXTRA_JUST_VIEW";
    public static final String EXTRA_FIRST_CREATE_ALBUM = "EXTRA_FIRST_CREATE_ALBUM";
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    public static final String EXTRA_DESCRIPTION_RETURN = "EXTRA_DESCRIPTION_RETURN";
    public static final int REQ_CODE_EDIT = 1200;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_edit_album_description_edt_description)
    TextView mDescription;

    private ToolbarButtonRightClickListener rightListener = new ToolbarButtonRightClickListener() {
        @Override
        public void onToolbarButtonRightClick(View view) {
            //retriever description album
            Bundle bundle = new Bundle();
            bundle.putString(EditAlbumDescriptionActivity.EXTRA_DESCRIPTION_RETURN, mDescription.getText().toString());
            Intent intent = new Intent().putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    /**
     * @param activity           from activity
     * @param view               view for animation transition
     * @param isOwn              check anbum album is my
     * @param desc               description of album
     * @param justView           if true --> just view description, cannot edit
     * @param isFirstCreateAlbum if true --> in edit description mode, show title other
     */
    public static void launch(AppCompatActivity activity, View view, boolean isOwn, String desc, boolean justView, boolean isFirstCreateAlbum) {
        ActivityOptionsCompat aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, VIEW_SHARE_ELEMENT);
        Intent intent = new Intent(activity, EditAlbumDescriptionActivity.class);
        intent.putExtra(EXTRA_IS_OWN, isOwn);
        intent.putExtra(EXTRA_DESCRIPTION, desc);
        intent.putExtra(EXTRA_JUST_VIEW, justView);
        intent.putExtra(EXTRA_FIRST_CREATE_ALBUM, isFirstCreateAlbum);
        activity.startActivityForResult(intent, REQ_CODE_EDIT, aoc.toBundle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_album_description;
    }

    @Override
    public void onCreateView(View rootView) {

        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        mDescription.setText(description);
    }

    @Override
    public void onViewReady() {
        super.onViewReady();
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        boolean justView = getIntent().getBooleanExtra(EXTRA_JUST_VIEW, false);
        boolean isFirstCreate = getIntent().getBooleanExtra(EXTRA_FIRST_CREATE_ALBUM, false);

        if (justView) {
            mDescription.setEnabled(false);
            mToolbar.setVisibilityButtonRight(false);
            mToolbar.setTitleCenter(R.string.view_description_activity);
        } else {
            mDescription.setEnabled(true);
            mToolbar.setVisibilityButtonRight(true);
            mToolbar.setButtonRightListener(rightListener);

            //change title album by isFirstCreateAlbum
            mToolbar.setTitleCenter(isFirstCreate ? R.string.view_description_activity : R.string.edit_description_activity);
            mDescription.requestFocus();
        }

    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DESCRIPTION_RETURN, mDescription.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}

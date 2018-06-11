package com.vn.ntsc.ui.profile.edit.hobby;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * Created by dev22 on 8/23/17.
 */
public class HobbyActivity extends BaseActivity implements ToolbarButtonRightClickListener {

    public static final String SELECTED_RESULT = "result";

    @BindArray(R.array.hobby)
    String[] listHobby;
    @BindView(R.id.activity_hobby_recycler_hobby)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    private HobbyAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hobby;
    }

    @Override
    public void onCreateView(View rootView) {
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setButtonRightListener(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HobbyAdapter();
        adapter.updateData(listHobby);
        recyclerView.setAdapter(adapter);

        int[] hobySelected = getIntent().getIntArrayExtra(SELECTED_RESULT);
        restoreState(hobySelected);
    }

    /**
     * restore previous state
     *
     * @param selectedPositions array position to set checked item
     */
    private void restoreState(int[] selectedPositions) {
        adapter.setSelectedPosition(selectedPositions);
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(SELECTED_RESULT, adapter.getSelectedPosition());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
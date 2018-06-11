package com.vn.ntsc.ui.profile.edit.job;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
public class JobActivity extends BaseActivity implements ToolbarButtonRightClickListener{

    public static final String SELECTED_RESULT = "result";
    public static final String IS_MALE = "gender_male";

    @BindArray(R.array.job_male)
    String[] listJobMale;
    @BindArray(R.array.job_female)
    String[] listJobFemale;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_job_recycler_job)
    RecyclerView recyclerView;

    private JobAdapter adapter;

    public static void start(AppCompatActivity activity, int selectedJob, boolean isMale, int requestCode) {
        Intent intent = new Intent(activity, JobActivity.class);
        intent.putExtra(IS_MALE, isMale);
        intent.putExtra(JobActivity.SELECTED_RESULT, selectedJob);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_job;
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

        adapter = new JobAdapter();
        boolean isMale = getIntent().getBooleanExtra(IS_MALE, false);
        adapter.updateData(isMale ? listJobMale : listJobFemale);
        recyclerView.setAdapter(adapter);

        restoreState(getIntent().getIntExtra(SELECTED_RESULT, -1));
    }

    /**
     * restore previous state
     *
     * @param selectedPosition item to check (single choice)
     */
    private void restoreState(int selectedPosition) {
        adapter.setSelectedPosition(selectedPosition);
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(SELECTED_RESULT, adapter.getSelectedPosition());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}


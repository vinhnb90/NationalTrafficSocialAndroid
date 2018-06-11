package com.vn.ntsc.ui.search.area;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.json.regions.RegionItem;
import com.vn.ntsc.repository.json.regions.Regions;
import com.vn.ntsc.utils.AssetsUtils;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hnc on 15/08/2017.
 */

public class ChooseEachAreaActivity extends BaseActivity {
    public static final String CHOOSE_AREAS_TYPE = "CHOOSE_AREAS_TYPE";

    public static final String SINGLE_CHOICE = "SINGLE_CHOICE";

    public static final String EXTRA_LIST_AREA = "EXTRA_LIST_AREA";

    public static final String EXTRA_RETURN_AFTER_SELECTED = "EXTRA_RETURN_AFTER_SELECTED";

    @BindView(R.id.fragment_choose_area_recycler_view_area)
    RecyclerView mRecyclerViewArea;
    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    private String chooseAreasType;

    private ArrayList<RegionItem> mAreas = new ArrayList<>();
    private ChooseEachAreaAdapter mAdapter;

    public static void launchRegions(AppCompatActivity activity, List<RegionItem> areas, @ActivityResultRequestCode int requestCode) {
        Intent intent = new Intent(activity, ChooseEachAreaActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_LIST_AREA, (ArrayList<? extends Parcelable>) areas);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_choose_area;
    }

    @Override
    public void onCreateView(View rootView) {
        List<RegionItem> data = getIntent().getParcelableArrayListExtra(EXTRA_LIST_AREA);

        if (data == null) {
            data = AssetsUtils.getDataAssets(this, Constants.PATH_REGIONS, Regions.class).regions;
        }
        data = convertBackgroundForItem(data);

        mAreas.addAll(data);
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setButtonRightListener(new ToolbarButtonRightClickListener() {
                    @Override
                    public void onToolbarButtonRightClick(View view) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(EXTRA_RETURN_AFTER_SELECTED, mAdapter.getData());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });
    }

    @Override
    public void onViewReady() {
        chooseAreasType = getIntent().getStringExtra(CHOOSE_AREAS_TYPE);
        mAdapter = new ChooseEachAreaAdapter(this, chooseAreasType);

        // set single choice from bundle
        boolean isSingleChoice = getIntent().getBooleanExtra(SINGLE_CHOICE, false);

        mAdapter.setSingleChoice(isSingleChoice);

        mAdapter.setData(mAreas);
        mRecyclerViewArea.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewArea.setAdapter(mAdapter);
    }

    public static List<RegionItem> getRegionsJsonAndConvertMatchAdapter() {
        List<RegionItem> regions = AssetsUtils.getDataAssets(AppController.getAppContext(), Constants.PATH_REGIONS, Regions.class).regions;
        return convertBackgroundForItem(regions);
    }

    public static List<RegionItem> convertBackgroundForItem(List<RegionItem> regionItems) {
        for (int i = 0; i < regionItems.size(); i++) {
            int adapterType;
            int code = regionItems.get(i).value;
            String name = regionItems.get(i).name;

            // Miền bắc, Miền trung, Miền Nam là title của Group
            // Background là Header (transparent)
            if (code == -1) {
                adapterType = ChooseEachAreaAdapter.TYPE_HEADER;
            }

            // Các item có code là 1,26,45 đứng đầu của group
            // Background là bo top
            // Tham khảo file assest/regions.xml
            else if (code == 1 || code == 26 || code == 45) {
                adapterType = ChooseEachAreaAdapter.TYPE_ROUND_TOP;
            }

            // Các item có code là 25,44,63 đứng cuối của group
            // Background là bo bottom
            // Tham khảo file assest/regions.xml
            else if (code == 25 || code == 44 || code == 63) {
                adapterType = ChooseEachAreaAdapter.TYPE_ROUND_BOTTOM;
            } else {
                adapterType = ChooseEachAreaAdapter.TYPE_ROUND_NORMAL;
            }


            regionItems.get(i).mAdapterType = adapterType;
            regionItems.get(i).name = name;
            regionItems.get(i).value = code;
        }

        return regionItems;
    }
}
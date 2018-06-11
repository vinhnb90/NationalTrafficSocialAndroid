package com.vn.ntsc.ui.profile.detail;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.json.boby.BodyType;
import com.vn.ntsc.repository.json.boby.BodyTypeItem;
import com.vn.ntsc.repository.json.hobby.Hobbies;
import com.vn.ntsc.repository.json.hobby.HobbyItem;
import com.vn.ntsc.repository.json.job.JobItem;
import com.vn.ntsc.repository.json.job.Jobs;
import com.vn.ntsc.repository.model.editprofile.Profile;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.AssetsUtils;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.widget.toolbar.ToolbarButtonLeftClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by dev22 on 9/7/17.
 * show profile only
 */
public class ProfileDetailActivity extends BaseActivity<ProfileDetailPresenter> implements ProfileDetailContract.View, ToolbarButtonLeftClickListener {
    /**
     * bundle key for fill data
     */
    private static final String INPUT_USER_ID = "input";

    private static final String INPUT_AVATAR = "avatar";

    private static final String INPUT_USER_INFO = "user.info";
    /**
     * key for set animation transform when change activity
     */
    private static final String ELEMENT_TRANSFORM = "elm";

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_profile_detail_avatar)
    ImageView avatar;
    @BindView(R.id.activity_profile_detail_parent_avatar)
    ImageView coverAvatar;
    @BindView(R.id.activity_profile_detail_name)
    TextView name;
    @BindView(R.id.activity_profile_detail_age)
    TextView age;
    @BindView(R.id.activity_profile_detail_gender)
    TextView gender;
    @BindView(R.id.activity_profile_detail_job)
    TextView job;
    @BindView(R.id.activity_profile_detail_region)
    TextView region;
    @BindView(R.id.activity_profile_detail_body_type)
    TextView bodyType;
    @BindView(R.id.activity_profile_detail_hobby)
    TextView hobby;
    @BindView(R.id.activity_profile_detail_message)
    TextView message;
    @BindView(R.id.activity_profile_detail_refresh)
    SwipeRefreshLayout refresh;

    private List<JobItem> jobsMale, jobsFeMale;
    private List<HobbyItem> hobbies;

    private UserInfoResponse userInfoResponse;
    private String userId;
    private BodyType bodyAsset;

    /**
     * launch my profile
     *
     * @param userId to get user info
     * @param view   for transform animation
     */
    public static void launch(AppCompatActivity activity, String userId, View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_TRANSFORM);
        Intent intent = new Intent(activity, ProfileDetailActivity.class);
        intent.putExtra(INPUT_USER_ID, userId);
        activity.startActivity(intent, options.toBundle());
    }

    public static void launch(AppCompatActivity activity, String userId) {
        Intent intent = new Intent(activity, ProfileDetailActivity.class);
        intent.putExtra(INPUT_USER_ID, userId);
        activity.startActivity(intent);
    }

    public static void launch(AppCompatActivity activity, UserInfoResponse userInfoResponse, View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_TRANSFORM);
        Intent intent = new Intent(activity, ProfileDetailActivity.class);
        intent.putExtra(INPUT_USER_INFO, userInfoResponse);
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_profile_detail;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        jobsMale = AssetsUtils.getDataAssets(this, Constants.PATH_JOBS, Jobs.class).jobsMale;
        jobsFeMale = AssetsUtils.getDataAssets(this, Constants.PATH_JOBS, Jobs.class).jobsFemale;
        hobbies = AssetsUtils.getDataAssets(this, Constants.PATH_HOBBIES, Hobbies.class).hobbies;

        userInfoResponse = getIntent().getParcelableExtra(INPUT_USER_INFO);
        userId = getIntent().getStringExtra(INPUT_USER_ID);
        String avatarUrl = getIntent().getStringExtra(INPUT_AVATAR);

        bodyAsset = AssetsUtils.getDataAssets(context, Constants.PATH_BODY_TYPE, BodyType.class);
        if (userInfoResponse != null) {
            userId = userInfoResponse.userId;
            Profile profile = new Profile(
                    userInfoResponse.userName,
                    userInfoResponse.avatar,
                    userInfoResponse.age,
                    userInfoResponse.gender,
                    userInfoResponse.job,
                    userInfoResponse.region,
                    userInfoResponse.bodyType,
                    userInfoResponse.hobby,
                    userInfoResponse.about
            );
            fillData(profile);
        } else {
            if (avatarUrl != null) {
                ImagesUtils.loadRoundedAvatar(avatarUrl, UserPreferences.getInstance().getGender(), avatar);
                ImagesUtils.loadCoverImage(context, avatarUrl, coverAvatar);
            }
        }

        // if userId is null or empty => out
        if (TextUtils.isEmpty(userId)) {
            throw new RuntimeException("NO USER ID");
        }

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().getUserInfo(userId, UserPreferences.getInstance().getToken());
            }
        });

        ViewCompat.setTransitionName(avatar, ELEMENT_TRANSFORM);
    }


    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setButtonLeftListener(this);

        getPresenter().getUserInfo(userId, UserPreferences.getInstance().getToken());
    }

    private void fillData(Profile profile) {
        if (profile != null) {
            mToolbar.setTitleCenter(profile.name);
            // load cover, avatar
            ImagesUtils.loadRoundedAvatar(profile.avatar, profile.gender, avatar);
            ImagesUtils.loadCoverImage(context, profile.avatar, coverAvatar);
        }

        // name
        assert profile != null;
        name.setText(profile.name);

        // age
        String strAge = profile.age + getString(R.string.edit_profile_age);
        age.setText(strAge);

        // gender
        gender.setText(profile.gender == 0 ? R.string.common_man : R.string.common_woman);

        // job
        if (profile.job != -1) {
            List<JobItem> jobList = profile.gender == 0 ? jobsMale : jobsFeMale;
            if (jobList.size() > profile.job)
                job.setText(jobList.get(profile.job).name);
        }

        // region
        region.setText(RegionUtils.getInstance(this).getRegionName(profile.region));

        // body type
        // if user unset server return -1
        if (profile.bodyType > 0) {
            // load array from json asset depend on gender of user
            List<BodyTypeItem> bodyTypes = profile.gender == 0 ? bodyAsset.bodyTypeMale : bodyAsset.bodyTypeFeMale;
            for (BodyTypeItem item : bodyTypes) {
                if (item.value == profile.bodyType) bodyType.setText(item.name);
            }
        }

        // hobby
        hobby.setText(profile.hobby);

        // message
        message.setText(profile.message);
    }

    @Override
    public void showLoadingDialog() {
        refresh.setRefreshing(true);
    }

    @Override
    public void hideLoadingDialog() {
        refresh.setRefreshing(false);
    }

    @Override
    public void update(GetUserInfoResponse response) {
        userInfoResponse = response.data;
        Profile profile = new Profile(
                userInfoResponse.userName,
                userInfoResponse.avatar,
                userInfoResponse.age,
                userInfoResponse.gender,
                userInfoResponse.job,
                userInfoResponse.region,
                userInfoResponse.bodyType,
                userInfoResponse.hobby,
                userInfoResponse.about
        );
        fillData(profile);
    }

    @Override
    public void onToolbarButtonLeftClick(View view) {
        finish();
    }
}


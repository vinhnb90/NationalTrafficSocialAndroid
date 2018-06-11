package com.vn.ntsc.ui.timeline.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileRequest;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileResponse;
import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.timeline.core.TimelineFragment;
import com.vn.ntsc.ui.timeline.user.header.TimelineProfileViewHolder;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nankai on 12/13/2017.
 */

public class TimelineProfileFragment extends TimelineFragment implements TimelineProfileViewHolder.ProfileListener {

    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------
    private boolean isFirstLoad = true;

    @Inject
    protected TimelineProfileViewHolder headerProfile;

    //----------------------------------------------------------------
    //------------------------ Instance ------------------------------
    //----------------------------------------------------------------
    public static TimelineProfileFragment newInstance(@TypeView.TypeViewTimeline int typeView) {
        Bundle args = new Bundle();
        TimelineProfileFragment fragment = new TimelineProfileFragment();
        args.putInt(BUNDLE_TYPE, typeView);
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getTimelineComponent().inject(this);
        super.onCreateView(rootView, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setRefreshing(true);

        initHelperTimelineUser();
        updateListPublicImages();
    }

    /**
     * update public images when delete image on TimeLineMediaDetail
     *
     * @see com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity#deleteBuzzSuccess(DeleteBuzzResponse, ListBuzzChild)
     */
    private void updateListPublicImages() {
        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_DATA, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                if (null != o && headerProfile != null && o instanceof List) {
                    try {
                        List<ListBuzzChild> childList = (List<ListBuzzChild>) o;
                        headerProfile.setListPublicImage(childList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (headerProfile != null)
            headerProfile.onDestroy();
    }

    //----------------------------------------------------------------
    //------------------------ Loading -------------------------------
    //----------------------------------------------------------------
    @Override
    public void onRefresh() {
        //when request userInfo well attach call function  requestList(), so do not call it in this case
        headerProfile.requestGetUserInfo(userInfo.userId);
    }

    //----------------------------------------------------------------
    //------------- TimelineProfileViewHolderListener ----------
    //----------------------------------------------------------------
    @Override
    public void setEvaluateUser(int value) {
        EvaluateUserProfileRequest profileRequest = new EvaluateUserProfileRequest(UserPreferences.getInstance().getToken(), userInfo.userId, value);
        getPresenter().getEvaluateUser(profileRequest);
    }

    //----------------------------------------------------------
    //----------------------Server event -----------------------
    //----------------------------------------------------------
    @Override
    public void onGetListPublicFile(PublicFileResponse response) {
        super.onGetListPublicFile(response);
        headerProfile.setListPublicImage(response.mData);
        listPublicFiles.onNext(response);
    }

    @Override
    public void onGetRoomLiveStream(BuzzListResponse response) {
    }

    @Override
    public void onEvaluateUser(EvaluateUserProfileResponse response) {
        super.onEvaluateUser(response);
        if (userInfo != null && headerProfile != null)
            headerProfile.requestGetUserInfo(userInfo.userId);
    }

    //----------------------------------------------------------
    //---------------------- Update data -----------------------
    //----------------------------------------------------------

    //----------------------------------------------------------------
    //------------------- Activity for result ------------------------
    //----------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityResultRequestCode.REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK)
            headerProfile.updateAvatar(UserPreferences.getInstance().getAva());
    }

    //----------------------------------------------------------------
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------
    public TimelineProfileFragment initHelperTimelineUser() {
        if (headerProfile.isInject())
            if (getActivity() instanceof MyProfileActivity) {
                LogUtils.i(TAG, "Init Helper Timeline User");
                headerProfile.setFragment(TimelineProfileFragment.this);
                headerProfile.setActivity((MyProfileActivity) getActivity());
                headerProfile.setTimelineHeaderProfileViewHolder(/*TimelineProfileViewHolderListener*/TimelineProfileFragment.this);
                headerProfile.initInjectView(adapter);
            }
        return this;
    }

    public TimelineProfileViewHolder getHeaderProfile() {
        return headerProfile;
    }

    public void requestListPublicImage() {

        if (!isFirstLoad) {
            headerProfile.requestListPublicFileData(this.userInfo.userId, 0);
            onRequestBuzzList(0);
        } else {
            headerProfile.requestListPublicFileData(TimelineProfileFragment.this.userInfo.userId, 0);
            onRequestBuzzList(1);
            isFirstLoad = false;
        }
    }

    public TimelineProfileFragment updateUserInfo() {
        LogUtils.i(TAG, "userInfo --> " + this.userInfo.userId);
        headerProfile.initUserInfo();
        //Update cache
        userInfoSubject.onNext(userInfo);
        return this;
    }

    @Override
    public void onItemTimelineClick(BuzzBean item, int position, View view) {

    }
}

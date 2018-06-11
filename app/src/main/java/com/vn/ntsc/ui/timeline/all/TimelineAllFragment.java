package com.vn.ntsc.ui.timeline.all;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzListRequest;
import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.timeline.livestream.TimelineLiveStreamFragment;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by nankai on 12/13/2017.
 */

public class TimelineAllFragment extends TimelineLiveStreamFragment {

    //----------------------------------------------------------------
    //------------------------ Instance ------------------------------
    //----------------------------------------------------------------
    public static TimelineAllFragment newInstance(@TypeView.TypeViewTimeline int typeView) {
        Bundle args = new Bundle();
        TimelineAllFragment fragment = new TimelineAllFragment();
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

        UserPreferences userPreferences = UserPreferences.getInstance();
        String token = userPreferences.getToken();
        double longitude = 0;
        double latitude = 0;

        BuzzListRequest buzzListRequest = new BuzzListRequest(token,
                null,
                TypeView.TypeViewTimeline.TIMELINE_LIVE_STREAM,
                false,
                longitude, latitude,
                0,
                TAKE_NUMBER);

        getPresenter().getRoomLiveStream(buzzListRequest, typeView, 0);

        onRequestBuzzList(0);
    }

    //----------------------------------------------------------------
    //------------------------ Loading -------------------------------
    //----------------------------------------------------------------
    @Override
    public void onRefresh() {
        super.onRefresh();

        UserPreferences userPreferences = UserPreferences.getInstance();
        String token = userPreferences.getToken();
        double longitude = 0;
        double latitude = 0;

        BuzzListRequest buzzListRequest = new BuzzListRequest(token,
                null,
                TypeView.TypeViewTimeline.TIMELINE_LIVE_STREAM,
                false,
                longitude, latitude,
                0,
                TAKE_NUMBER);

        getPresenter().getRoomLiveStream(buzzListRequest, typeView, 0);
    }

    //----------------------------------------------------------
    //---------------------- Update data -----------------------
    //----------------------------------------------------------

    @Override
    public void onItemTimelineClick(BuzzBean item, int position, View view) {

    }

    @Override
    public void onRetryBuzzDetailRequest(BuzzDetailRequest request, String templateId, int position, View view) {
        adapter.updateErrorRequestBuzzDetail(request, templateId, false);
        getPresenter().getBuzzDetail(request, templateId);
    }

    @Override
    protected void onAddTemplate(BuzzBean templatePostBean) {
        templatePostBean.isTemplate = true;
        adapter.addFirst(templatePostBean);
    }

    @Override
    public void onBuzzListResponse(BuzzListResponse response) {
        response.data = updateTemplate(response.data);
        super.onBuzzListResponse(response);
    }

    //----------------------------------------------------------------
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------

    /**
     * Put in an array and check if the adapter has an isTemplate then keep it and insert it into the new array
     *
     * @param buzzData List<BuzzBean>
     * @return List<BuzzBean>
     */
    private List<BuzzBean> updateTemplate(final List<BuzzBean> buzzData) {

        Observable.fromIterable(adapter.getData())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                })
                .filter(new Predicate<BuzzBean>() {
                    @Override
                    public boolean test(BuzzBean dataBean) throws Exception {
                        return dataBean.isTemplate();
                    }
                })
                .forEach(new Consumer<BuzzBean>() {
                    @Override
                    public void accept(BuzzBean dataBean) throws Exception {
                        buzzData.add(0, dataBean);
                    }
                });

        return buzzData;
    }

    @Override
    protected void requestBuzzDetail(String buzzId, String templateId) {
        UserPreferences userPreference = new UserPreferences();
        BuzzDetailRequest buzzDetailRequest = new BuzzDetailRequest(userPreference.getToken(), buzzId);
        getPresenter().getBuzzDetail(buzzDetailRequest, templateId);
    }

    @Override
    protected void setObserverEventBus() {
        super.setObserverEventBus();

        //------------- Post status --------------
        //Called when Post status is in PREPARING KEY_DATA AND STATUS KEY_DATA
        RxEventBus.subscribe(SubjectCode.SUBJECT_POST_STATUS, this, new Consumer<Object>() {
            @Override
            public void accept(final Object o) throws Exception {
                onAddTemplate((BuzzBean) o);
            }
        });

        //Called when Post status is error
        RxEventBus.subscribe(SubjectCode.SUBJECT_POST_STATUS_ERROR, this, new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                LogUtils.i(TAG, "SUBJECT_POST_STATUS_ERROR");
                try {
                    PostStatusResponse postStatusResponse = (PostStatusResponse) o;
                    adapter.removeTemplate(postStatusResponse.tempId);
                    if (adapter.getData().size() <= 0) {
                        adapter.setEmptyView(R.layout.layout_empty);
                    }
                    onServerResponseInvalid(postStatusResponse.code, postStatusResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Called when Post status is successful
        RxEventBus.subscribe(SubjectCode.SUBJECT_POST_STATUS_SUCCESS, this, new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                LogUtils.i(TAG, "SUBJECT_POST_STATUS_SUCCESS");
                try {
                    PostStatusResponse postStatusResponse = (PostStatusResponse) o;
                    requestBuzzDetail(postStatusResponse.data.buzzId, postStatusResponse.tempId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //------------- End Post status --------------
    }
}

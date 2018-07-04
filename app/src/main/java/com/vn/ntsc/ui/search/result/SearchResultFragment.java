package com.vn.ntsc.ui.search.result;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.json.regions.RegionItem;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.search.MeetPeopleBean;
import com.vn.ntsc.repository.model.search.MeetPeopleRequest;
import com.vn.ntsc.repository.model.search.MeetPeopleResponse;
import com.vn.ntsc.repository.model.search.byname.SearchByNameRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.search.SearchSetting;
import com.vn.ntsc.ui.search.SearchSettingActivity;
import com.vn.ntsc.ui.search.byname.SearchByNameActivity;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.vn.ntsc.repository.TypeView.SearchType.TYPE_BY_NAME;
import static com.vn.ntsc.repository.TypeView.SearchType.TYPE_SETTING;

/**
 * Created by hnc on 22/08/2017.
 */

public class SearchResultFragment extends BaseFragment<SearchResultPresenter> implements SearchResultContract.View, SwipeRefreshLayout.OnRefreshListener, MultifunctionAdapter.RequestLoadMoreListener {

    public static final String BUNDLE_SEARCH_BY_SETTING = "BUNDLE_SEARCH_BY_SETTING";
    public static final String BUNDLE_SEARCH_BY_NAME = "BUNDLE_SEARCH_BY_NAME";

    @BindView(R.id.fragment_search_result_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.fragment_search_result_recycler_view)
    RecyclerView mRecyclerViewSearchResult;

    private int mType;
    private SearchResultAdapter mResultAdapter;
    private String nameSearch;
    private SearchSetting settingSearch;

    /**
     * Number of items per request
     */
    private int mSkip = 0;


    /**
     * firstLoad = true : load when open this fragment, if repose.size = 0 --> request not have result --> open dialog turn back SearchSettingActivity
     * firstLoad = false: Has loadMore at least once
     */
    private boolean isFirstRequest;


    /**
     * Khi tích vào icon Favorite ta sẽ đánh dấu position của item đó
     * Đợi server trả kết quả về thì dùng biến này để xử lý: favorite thành công hay remove thành công....
     */
    private int positionWaiting;
    private String bodyType;
    private String NONE = "";


    public static SearchResultFragment newInstance(SearchSetting searchBySetting, String body) {
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_SEARCH_BY_SETTING, searchBySetting);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.bodyType = body;
        fragment.setArguments(args);
        return fragment;
    }


    public static SearchResultFragment newInstance(String searchByName) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_SEARCH_BY_NAME, searchByName);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_result;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getModulesCommonComponent().inject(this);
    }

    @Override
    protected void setUserVisibleHint() {

        Bundle bundle = getArguments();
        nameSearch = bundle.getString(BUNDLE_SEARCH_BY_NAME);
        settingSearch = bundle.getParcelable(BUNDLE_SEARCH_BY_SETTING);

        isFirstRequest = true;

        if (nameSearch == null && settingSearch != null) {
            mType = TYPE_SETTING;
            mSkip = 0;// first request --> skip == 0 ;
            requestSearchBySetting(settingSearch, mSkip);

        } else if (nameSearch != null && settingSearch == null) {
            mType = TYPE_BY_NAME;
            mSkip = 0; // first request --> skip == 0 ;
            requestSearchByName(nameSearch, mSkip);
        }

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        mResultAdapter = new SearchResultAdapter(mIOnItemClickListener);
        mResultAdapter.setEnableLoadMore(true);
        mResultAdapter.setOnLoadMoreListener(this, mRecyclerViewSearchResult);

        mRecyclerViewSearchResult.setHasFixedSize(true);
        mRecyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        //1dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mRecyclerViewSearchResult.addItemDecoration(new SpacesItemDecoration(space));
        mRecyclerViewSearchResult.setAdapter(mResultAdapter);
        mResultAdapter.setMenuItemClick(mIOnItemMenuClickListener);
    }

    @Override
    public void onRefresh() {

        // clear old data
        mResultAdapter.getData().clear();
        mResultAdapter.notifyDataSetChanged();

        mSkip = 0; // when refresh, skip should be = 0

        switch (mType) {
            case TYPE_SETTING:
                requestSearchBySetting(settingSearch, mSkip);
                break;

            case TYPE_BY_NAME:
                requestSearchByName(nameSearch, mSkip);
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        // take more 20 data for append to list view
        mSkip += 20;
        isFirstRequest = false;

        switch (mType) {
            case TYPE_SETTING:
                requestSearchBySetting(settingSearch, mSkip);
                break;

            case TYPE_BY_NAME:
                requestSearchByName(nameSearch, mSkip);
                break;
        }
    }

    /**
     * Request search
     *
     * @param settingSearch: search with options of settingSearch
     */
    private void requestSearchBySetting(SearchSetting settingSearch, int skip) {
        if (bodyType == null) {
            if (settingSearch.getCurrentBodyType().value == -1) {
                bodyType = NONE;
            } else {
                bodyType = String.valueOf(settingSearch.getCurrentBodyType().value);
            }
        }

        MeetPeopleRequest request =
                new MeetPeopleRequest(settingSearch.getMinAge(),
                        settingSearch.getMaxAge(),
                        getRegionsCodeFromSelectedList(settingSearch.getSelectedRegions()),
                        settingSearch.isLoginWithin24h(),
                        settingSearch.getCurrentOrderSort().value,
                        settingSearch.getCurrentGender().value,
                        bodyType,
                        settingSearch.getCurrentAvatar().value,
                        settingSearch.isNoInteracted(),
                        UserPreferences.getInstance().getToken(), skip);
        getPresenter().searchBySetting(request);
    }

    /**
     * Request search
     *
     * @param nameSearch: search by name
     */
    private void requestSearchByName(String nameSearch, int skip) {
        SearchByNameRequest request = new SearchByNameRequest(nameSearch, UserPreferences.getInstance().getToken(), skip);
        getPresenter().searchByName(request);
    }

    private void showDialogEmptyData() {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.search_setting_not_found)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getActivity(), SearchSettingActivity.class));
                        getActivity().finish();
                        dialogInterface.dismiss();
                    }
                })
                .onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }


    //======================================= Listener Adapter =====================================

    private SearchResultAdapter.IOnItemClickListener mIOnItemClickListener = new SearchResultAdapter.IOnItemClickListener() {
        @Override
        public void onItemClick(View view, MeetPeopleBean itemBean, int position) {
            UserInfoResponse userProfileBean = new UserInfoResponse(itemBean.userId, itemBean.gender, itemBean.userName, itemBean.avaId, itemBean.isFav);
            MyProfileActivity.launch((AppCompatActivity) getActivity(), view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
        }
    };

    private SearchResultAdapter.IOnItemMenuClickListener mIOnItemMenuClickListener = new SearchResultAdapter.IOnItemMenuClickListener() {
        @Override
        public void onMenuItemClick(View view, MeetPeopleBean item, int position) {

            String token = UserPreferences.getInstance().getToken();
            String userId = item.userId;
            positionWaiting = position;

            // 0: chưa đc like
            // 1: đã like
            if (item.isFav == 0) {
                AddFavoriteRequest request = new AddFavoriteRequest(token, userId);
                getPresenter().addFavorite(request);
            } else {
                RemoveFavoriteRequest request = new RemoveFavoriteRequest(token, userId);
                getPresenter().removeFavorite(request);
            }
        }
    };


    public void addDataCallback(MeetPeopleBean meetPeopleBean) {
        Activity activity = getActivity();
        if (activity instanceof SearchSettingActivity) {
            ((SearchSettingActivity) activity).addDataCallback(meetPeopleBean);

        } else if (activity instanceof SearchByNameActivity) {
            ((SearchByNameActivity) activity).addDataCallback(meetPeopleBean);
        }
    }

    public void removeDataCallback(MeetPeopleBean meetPeopleBean) {
        Activity activity = getActivity();
        if (activity instanceof SearchSettingActivity) {
            ((SearchSettingActivity) activity).removeDataCallback(meetPeopleBean);

        } else if (activity instanceof SearchByNameActivity) {
            ((SearchByNameActivity) activity).removeDataCallback(meetPeopleBean);
        }
    }

    //======================================= Server Callback ======================================

    @Override
    public void addFavoriteSuccess() {
        Toast.makeText(getContext(), getContext().getString(R.string.search_result_favorited), Toast.LENGTH_SHORT).show();
        mResultAdapter.updateBackgroundItemFavorite(positionWaiting);
        addDataCallback(mResultAdapter.getData(positionWaiting));
    }

    @Override
    public void handleErrorUserNotFound() {
        Toast.makeText(getContext(), getContext().getString(R.string.search_result_add_favorite_user_not_found), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleErrorUserIsBlocked() {
        Toast.makeText(getContext(), getContext().getString(R.string.search_result_add_favorite_fail), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void removeFavoriteSuccess() {
        Toast.makeText(getContext(), getContext().getString(R.string.search_result_un_favorited), Toast.LENGTH_SHORT).show();
        mResultAdapter.updateBackgroundItemFavorite(positionWaiting);

        // Thằng ôn này vừa bị unlike nên xóa khỏi mảng nếu nó đc add trước đấy
        removeDataCallback(mResultAdapter.getData(positionWaiting));
    }

    @Override
    public void onSearchSuccess(MeetPeopleResponse response) {
        Log.e("onSearchSuccess", "mType" + mType);
        if (response.mData.isEmpty() && isFirstRequest) {
            showDialogEmptyData();
        } else if (response.mData.isEmpty() && !isFirstRequest) {
            mResultAdapter.loadMoreEmpty();
        } else {
            mResultAdapter.addData(response.mData);

        }
        //TODO 11962#note-5: Update Search by Name title - Updated by Robert on 2018 May 16
        if (activity instanceof SearchByNameActivity) {
            ((SearchByNameActivity) activity).setSearchByNameTitle(getString(R.string.title_activity_search_result));
        }
    }

    @Override
    public void onFinish() {
        mRefreshLayout.setRefreshing(false);
        mResultAdapter.loadMoreComplete();
    }

    //======================================= End callback =========================================

    private int[] getRegionsCodeFromSelectedList(List<RegionItem> items) {

        List<Integer> code = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isChecked()) {
                code.add(items.get(i).value);
            }
        }

        int[] regionsCode = new int[code.size()];

        for (int i = 0; i < code.size(); i++) {
            regionsCode[i] = code.get(i);
        }

        return regionsCode;
    }
}

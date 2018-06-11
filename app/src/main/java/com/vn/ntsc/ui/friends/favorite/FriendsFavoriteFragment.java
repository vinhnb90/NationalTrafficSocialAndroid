package com.vn.ntsc.ui.friends.favorite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteBean;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.views.popup.PopupMenuContext;

import butterknife.BindView;

/**
 * Created by hnc on 08/08/2017.
 */

public class FriendsFavoriteFragment extends BaseFragment<FriendsFavoritePresenter> implements
        FavoriteEventClickListener<FriendsFavoriteBean>, SwipeRefreshLayout.OnRefreshListener,
        MultifunctionAdapter.RequestLoadMoreListener, FriendsFavoriteContract.View {

    public static final String BUNDLE_FAVORITE_TYPE = "BUNDLE_FAVORITE_TYPE";

    private int mTypeFavorite;

    @BindView(R.id.fragment_friend_favorited_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.fragment_friend_favorited_recycler_view)
    RecyclerView mRecyclerViewFriendsFavorite;


    private static final int TAKE = 20;
    private FriendsFavoriteAdapter mAdapter;
    private FriendsFavoriteBean friendWillUnFavorite;
    private int skip = 0;


    public static FriendsFavoriteFragment newInstance(@TypeView.TypeViewFavorite int favoriteType) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_FAVORITE_TYPE, favoriteType);
        FriendsFavoriteFragment fragment = new FriendsFavoriteFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend_favorited;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTypeFavorite = getArguments().getInt(BUNDLE_FAVORITE_TYPE);
        getModulesCommonComponent().inject(this);

        mAdapter = new FriendsFavoriteAdapter(this);
        mAdapter.setEnableLoadMore(true);

        mAdapter.setOnLoadMoreListener(this, mRecyclerViewFriendsFavorite);

        mRecyclerViewFriendsFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewFriendsFavorite.setHasFixedSize(true);
        //1dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mRecyclerViewFriendsFavorite.addItemDecoration(new SpacesItemDecoration(space));

        mRecyclerViewFriendsFavorite.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void setUserVisibleHint() {
        // Request again when swipe down
        mAdapter.getData().clear();  // remove all data
        skip = 0;
        request();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // RecyclerView item click
    @Override
    public void onItemClick(FriendsFavoriteBean itemBean, View view, int position) {
        UserInfoResponse userProfileBean = new UserInfoResponse(itemBean.userId, itemBean.gender, itemBean.userName, itemBean.thumbnailUrl, itemBean.isFav);
        MyProfileActivity.launch((AppCompatActivity) getActivity(), view, userProfileBean, TypeView.ProfileType.COME_FROM_OTHER);
    }

    // Menu item click

    /**
     * @param data     data of item in recycler view
     * @param view     button menu (view.getParent() = itemView of RecyclerView)
     * @param position position of menu in recycler view
     */
    @Override
    public void onMenuItemClick(final FriendsFavoriteBean data, final View view, int position) {

        PopupMenuContext menuContext = new PopupMenuContext
                .PopupBuilder(getContext(), view, R.menu.menu_friends_fav)
                .setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_send_message:
                                break;

                            case R.id.action_un_favorite:
                                requestRemoveFriendMeFavorited(data);
                                break;
                        }
                        return false;
                    }
                })
                .build();


        // Nếu đây là tab "Những người thích tôi" thì ẩn cái item menu "Bỏ yêu thích"
        // vì nó thích mình làm sao mà bắt nó bỏ được
        if (mTypeFavorite == TypeView.TypeViewFavorite.FAVORITE_ME) {
            menuContext.getMenu().findItem(R.id.action_un_favorite).setVisible(false);
        }
    }

    @Override
    public void onRefresh() {
        // Request again when swipe down
        mAdapter.getData().clear();  // remove all data
        skip = 0;
        request();
    }

    @Override
    public void onLoadMoreRequested() {
        // load more data for append to recyclerView
        skip = mAdapter.getData().size();
        request();
    }

    // request danh sách bạn bè mà tôi thích
    private void requestListFriendsMeFavorite(int skip) {
        mSwipeRefreshLayout.setRefreshing(true);
        String token = UserPreferences.getInstance().getToken();
        FriendsFavoriteRequest request = new FriendsFavoriteRequest(FriendsFavoriteRequest.apiListFriendMeFav, token, TAKE, skip);
        getPresenter().requestListFriendsMeFavorite(request);
    }

    // request danh sách bạn bè mà thích tôi
    private void requestListFriendsFavoriteMe(int skip) {
        mSwipeRefreshLayout.setRefreshing(true);
        String token = UserPreferences.getInstance().getToken();
        FriendsFavoriteRequest request = new FriendsFavoriteRequest(FriendsFavoriteRequest.apiListFriendFavMe, token, TAKE, skip);
        getPresenter().requestListFriendsFavoriteMe(request);
    }

    // remove bạn mà tôi đã thích
    // xảy ra khi bấm vào menu "Bỏ yêu thích" ở mỗi item
    // Khi request tới server "Hủy yêu thích" gán thằng định xóa vào biến: friendWillUnFavorite để xử lý xóa ở local
    private void requestRemoveFriendMeFavorited(FriendsFavoriteBean bean) {
        String token = UserPreferences.getInstance().getToken();
        RemoveFavoriteRequest request = new RemoveFavoriteRequest(token, bean.userId);
        getPresenter().removeFriendsMeFavorite(request);
        friendWillUnFavorite = bean;
    }

    // request common
    private void request() {
        if (mTypeFavorite == TypeView.TypeViewFavorite.FAVORITE_ME) {
            requestListFriendsFavoriteMe(skip);

        } else if (mTypeFavorite == TypeView.TypeViewFavorite.ME_FAVORITE) {
            requestListFriendsMeFavorite(skip);

        }
    }

    //======================================== Server call back====================================
    @Override
    public void onFriendsMeFavResponse(FriendsFavoriteResponse response) {
        // xảy ra trên tab: Những người mà tôi favorite
        if (response.listFavorites.isEmpty() && mAdapter.getData().size() <= 0) {
            mAdapter.setEmptyView(R.layout.layout_empty);
        } else if (response.listFavorites.isEmpty()) {
            mAdapter.loadMoreEmpty();
        } else {
            mAdapter.addData(response.listFavorites);
        }
    }


    @Override
    public void onFriendsFavMeResponse(FriendsFavoriteResponse response) {
        // xảy ra trên tab: Những người đã favorite tôi
        if (response.listFavorites.isEmpty() && mAdapter.getData().size() <= 0) {
            mAdapter.setEmptyView(R.layout.layout_empty);
        } else if (response.listFavorites.isEmpty()) {
            mAdapter.loadMoreEmpty();
        } else {
            mAdapter.addData(response.listFavorites);
        }
    }

    @Override
    public void onRemoveFriendsMeFavResponse(RemoveFavoriteResponse response) {
        // sau khi server xác nhận "Hủy yêu thích" thành công thì, xóa thằng đó trên adapter của local
        if (response.code == 0 && friendWillUnFavorite != null) {
            int position = mAdapter.getData().indexOf(friendWillUnFavorite);
            mAdapter.remove(position);
            mAdapter.notifyItemChanged(position);
            friendWillUnFavorite = null;
        }

    }

    @Override
    public void onCompleted() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);

        if (mAdapter.getData().size() >= TAKE) {
            mAdapter.loadMoreEmpty();
        } else {
            mAdapter.loadMoreEnd();
        }

        mAdapter.loadMoreComplete();

        if (mAdapter.getData().isEmpty()) {
            mAdapter.setEmptyView(R.layout.layout_empty);
        }
    }
    //=========================================== End ==============================================
}

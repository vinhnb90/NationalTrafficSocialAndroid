package com.vn.ntsc.ui.tagfriends;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteBean;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteRequest;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.toolbar.ToolbarButtonLeftClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarButtonRightClickListener;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Robert on 2017 Sep 13.
 */
public class TagFriendActivity extends BaseActivity<TagFriendPresenter> implements TagFriendContract.View,
        TagFriendEventListener<ListTagFriendsBean>, SwipeRefreshLayout.OnRefreshListener,
        TokenCompleteTextView.TokenListener<ListTagFriendsBean>
        , ToolbarButtonRightClickListener, ToolbarButtonLeftClickListener {

    private final String TAG = TagFriendActivity.class.getSimpleName();

    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------
    private static final String RESULT_KEY = "tag_friend_result";
    private static final String EXTRA_DATA = "extra.data";
    private static final String EXTRA_HAS_EDIT_TAG_FRIENDS = "extra.has.edit";

    private TagFriendAdapter mTagFriendAdapter;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_tag_friend_edt_search_autocomplete)
    TagFriendCompletionView mAutoCompleteTextView;
    @BindView(R.id.activity_tag_friend_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_tag_friend_recycler_view_tag_friend)
    RecyclerView mRecyclerViewFriendsFavorite;
    @BindView(R.id.activity_tag_friend_layout_search)
    ConstraintLayout layoutSearch;

    ArrayList<ListTagFriendsBean> mTaggedFriend = new ArrayList<>();
    private boolean hasEditTagFriends = true;

    public static void startActivityForResult(Activity mBaseActivity, String resultKey, ArrayList<ListTagFriendsBean> mTaggedFriend, @ActivityResultRequestCode int requestCode) {
        Intent intent = new Intent(mBaseActivity, TagFriendActivity.class);
        intent.putExtra(RESULT_KEY, resultKey);
        intent.putParcelableArrayListExtra(EXTRA_DATA, mTaggedFriend);
        mBaseActivity.startActivityForResult(intent, requestCode);
    }

    public static void launch(Activity mBaseActivity, ArrayList<ListTagFriendsBean> mTaggedFriend, boolean hasEditTagFriends) {
        Intent intent = new Intent(mBaseActivity, TagFriendActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_DATA, mTaggedFriend);
        intent.putExtra(EXTRA_HAS_EDIT_TAG_FRIENDS, hasEditTagFriends);
        mBaseActivity.startActivity(intent);
    }

    //----------------------------------------------------------------
    //------------------------ View  ---------------------------------
    //----------------------------------------------------------------
    @Override
    public int getLayoutId() {
        return R.layout.activity_tag_friend;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    public void onCreateView(View rootView) {

        getModulesCommonComponent().inject(this);

        hasEditTagFriends = getIntent().getBooleanExtra(EXTRA_HAS_EDIT_TAG_FRIENDS, true);

        mTagFriendAdapter = new TagFriendAdapter(this);

        //Set the LayoutManager that this RecyclerView will use.
        mRecyclerViewFriendsFavorite.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewFriendsFavorite.setHasFixedSize(true);
        mRecyclerViewFriendsFavorite.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        mRecyclerViewFriendsFavorite.setDrawingCacheEnabled(true);
//        mRecyclerViewFriendsFavorite.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        /*
         * When List friend favorites is empty crashes
         */
        mTagFriendAdapter.bindToRecyclerView(mRecyclerViewFriendsFavorite);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mTagFriendAdapter.setEnableLoadMore(false);
        mTaggedFriend = getIntent().getParcelableArrayListExtra(EXTRA_DATA);
    }

    @Override
    public void onViewReady() {
        Utils.dumpIntent(getIntent());

        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true)
                .setButtonRightListener(this)
                .setButtonLeftListener(this);

        if (hasEditTagFriends) {
            mToolbar.setVisibilityButtonRight(true);
            mSwipeRefreshLayout.setRefreshing(true);
            getListFriendsMeFavorite();

            mToolbar.setTitleCenter(R.string.tag_friend_title);
            //Hide soft keyboard if need
            mAutoCompleteTextView.setFocusable(true);
            mAutoCompleteTextView.requestLayout();
            mAutoCompleteTextView.setOnEditorActionListener(doneAction);
            mAutoCompleteTextView.addTextChangedListener(mAutoCompleteTextChangedListener);
            //Set the listener that will be notified of changes in the TokenList
            mAutoCompleteTextView.setTokenListener(this);
            //Set the action to be taken when a Token is clicked
            mAutoCompleteTextView.setTokenClickStyle(hasEditTagFriends
                    ? TokenCompleteTextView.TokenClickStyle.Delete
                    : TokenCompleteTextView.TokenClickStyle.Select);
        } else {
            mToolbar.setVisibilityButtonRight(false);
            if (mTaggedFriend != null)
                mTagFriendAdapter.setNewData(mTaggedFriend);
            mAutoCompleteTextView.setVisibility(View.GONE);
            mToolbar.setTitleCenter(R.string.list_tag_friends);
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(false);
            mAutoCompleteTextView.setVisibility(View.GONE);
            layoutSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart(View viewRoot) {
        super.onStart(viewRoot);
        if (hasEditTagFriends) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (ListTagFriendsBean item : mTaggedFriend) {
                        mAutoCompleteTextView.addObject(item);
                        mAutoCompleteTextView.getViewForObject(item);
                    }
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------
    //------------------------ Loading -------------------------------
    //----------------------------------------------------------------

    @Override
    public void onRefresh() {
        getListFriendsMeFavorite();
    }

    private TextWatcher mAutoCompleteTextChangedListener = new TextWatcher() {
        /**
         * This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
         * It is an error to attempt to make changes to s from this callback.
         * @param charSequence
         * @param start
         * @param count
         * @param after
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        /**
         * This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
         * It is an error to attempt to make changes to s from this callback.
         * @param charSequence
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            LogUtils.i(TAG, "Text Changed : " + charSequence.toString());
            autoFilterCompleteHandler(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextView.OnEditorActionListener doneAction = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Hide soft keyboard if need
                KeyboardUtils.hideSoftKeyboard(context, mAutoCompleteTextView);
                return true;
            }
            return false;
        }
    };

    //----------------------------------------------------------
    //----------------------Server event -----------------------
    //----------------------------------------------------------

    @Override
    public void onFriendsFavMeResponse(final TagFriendsFavoriteResponse response) {
        if (!response.listFavorites.isEmpty() && response.listFavorites.size() > 0) {
            mTagFriendAdapter.setNewData(replaceListToListTagFriendsBean(response.listFavorites));
        } else {
            mAutoCompleteTextView.clear();
            mTagFriendAdapter.setEmptyView(R.layout.layout_empty);
        }
    }

    @Override
    public void onGetFavYourSelfComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    //----------------------------------------------------------------
    //------------------------ TagFriendAdapter ----------------------
    //----------------------------------------------------------------

    /**
     * When user click to friend for choose tag
     *
     * @param friendItem
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(ListTagFriendsBean friendItem, View view, int position) {
        if (hasEditTagFriends) {
            bindTaggedFriendToView(friendItem);
        } else {
            MyProfileActivity.launch(this, mAutoCompleteTextView, friendItem.userId, TypeView.ProfileType.COME_FROM_TIMELINE_BY_ID);
        }
    }

    //----------------------------------------------------------------
    //------------------------ CompleteTextView ----------------------
    //----------------------------------------------------------------
    @Override
    public void onTokenAdded(ListTagFriendsBean friendItem) {

    }

    @Override
    public void onTokenRemoved(final ListTagFriendsBean friendItem) {

        mTaggedFriend.remove(friendItem);

        mTagFriendAdapter.addData(0, friendItem);

        mRecyclerViewFriendsFavorite.requestLayout();
    }

    //----------------------------------------------------------------
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------

    /**
     * Bind Tagged friend to mAutoCompleteTextView
     *
     * @param friendItem The TagFriendsFavoriteBean item
     */
    private void bindTaggedFriendToView(ListTagFriendsBean friendItem) {
        if (mAutoCompleteTextView.getObjects() != null && !mAutoCompleteTextView.getObjects().contains(friendItem)) {

            mTaggedFriend.add(friendItem);

            mTagFriendAdapter.getData().remove(friendItem);
            mTagFriendAdapter.notifyDataSetChanged();

            mRecyclerViewFriendsFavorite.requestLayout();

            mAutoCompleteTextView.addObject(friendItem);
            mAutoCompleteTextView.requestLayout();
        }
    }

    /**
     * Get list friend were favorites of yourself
     */
    private void getListFriendsMeFavorite() {
        if (!hasEditTagFriends) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);

        String token = UserPreferences.getInstance().getToken();
        TagFriendsFavoriteRequest request = new TagFriendsFavoriteRequest(TagFriendsFavoriteRequest.apiListFriendMeFav, token, 0, 0);
        getPresenter().getListFriendsMeFavorite(request);
    }

    private List<ListTagFriendsBean> replaceListToListTagFriendsBean(List<TagFriendsFavoriteBean> tagFriendsFavoriteBeans) {

        List<ListTagFriendsBean> listTagFriendsBeans = new ArrayList<>();
        for (TagFriendsFavoriteBean bean : tagFriendsFavoriteBeans) {
            ListTagFriendsBean item = new ListTagFriendsBean();
            item.userId = bean.userId;
            item.userName = bean.userName;
            item.avatar = bean.originalUrl;
            item.age = bean.age;
            item.gender = bean.gender;
            item.region = bean.region;
            listTagFriendsBeans.add(item);
        }

        //Set Default List Tag friends
        mTagFriendAdapter.setDataFinal(listTagFriendsBeans);

        List<ListTagFriendsBean> removeTagFriendsOutList = new ArrayList<>();
        boolean hasAdded;
        for (ListTagFriendsBean tagItem : listTagFriendsBeans) {
            hasAdded = true;
            for (ListTagFriendsBean item : mTaggedFriend) {
                if (tagItem.userId.equals(item.userId)) {
                    hasAdded = false;
                }
            }

            if (hasAdded)
                removeTagFriendsOutList.add(tagItem);
        }

        return removeTagFriendsOutList;
    }

    /**
     * Automatically filter and complete friend
     *
     * @param charSequence the String input to quick search
     */
    public void autoFilterCompleteHandler(final CharSequence charSequence) {

        final String quickSearch = Utils.nullToEmpty(Utils.replaceAll(charSequence.toString(), ",", "")).trim();
        if (mTagFriendAdapter == null)
            return;

        //If user enter to input text to quick filter friend
        mTagFriendAdapter.setDataTag(mTaggedFriend).getFilter().filter(quickSearch);
    }

    @Override
    public void onToolbarButtonLeftClick(View view) {
        finish();
    }

    @Override
    public void onToolbarButtonRightClick(View view) {
        //Set tagged friend list result to PostStatusActivity
        Intent returnIntent = getIntent();
        if (hasEditTagFriends) {
            returnIntent.putParcelableArrayListExtra(returnIntent.getStringExtra(RESULT_KEY), mTaggedFriend);
            setResult(RESULT_OK, returnIntent);
        }
        finish();
    }
}

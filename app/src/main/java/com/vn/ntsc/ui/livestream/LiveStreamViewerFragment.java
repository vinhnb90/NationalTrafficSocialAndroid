package com.vn.ntsc.ui.livestream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.model.token.CheckTokenResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.ui.comment.CommentActivity;
import com.vn.ntsc.utils.AnimationUtils;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardHeightObserver;
import com.vn.ntsc.utils.keyboard.KeyboardHeightProvider;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_ADD_TAG_FRIEND_FOR_LIVE_STREAM;

/**
 * Created by nankai on 12/27/2017.
 */

public class LiveStreamViewerFragment extends BaseFragment<LiveStreamPresenter> implements LiveStreamContract.View, KeyboardHeightObserver {

    private static final String TAG = LiveStreamViewerFragment.class.getSimpleName();

    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------
    private CompositeDisposable disposables;
    KeyboardHeightProvider mKeyboardHeightProvider;
    private final int TAKE_COMMENT = 20;
    private boolean isOpen;

    /**
     * for receive data when pick friends to share
     *
     * @see #onActivityResult(int, int, Intent)
     */
    private final String TAG_FRIEND_LIST_FOR_LIVE_STREAM_RESULT = "TAG_FRIEND_LIST_FOR_LIVE_STREAM_RESULT";

    @BindView(R.id.layout_live_stream_header_navigation_bar)
    ConstraintLayout navigationBar;

    @BindView(R.id.layout_live_stream_header_layout_time_video)
    LinearLayout layoutTimeView;
    @BindView(R.id.layout_live_stream_header_time)
    TextView mTime;
    @BindView(R.id.layout_live_stream_header_font_back_camera)
    ImageView liveStreamFontBackCamera;
    @BindView(R.id.layout_live_stream_header_view_number)
    TextViewVectorCompat mViewNumber;

    @BindView(R.id.fragment_live_stream_viewer_send_comment)
    ImageView sendComment;
    @BindView(R.id.live_stream_comment)
    EditText edtComment;

    @BindView(R.id.layout_live_stream_comment_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.layout_live_stream_comment_list_comment)
    RecyclerView listComment;
    @BindView(R.id.layout_live_stream_comment_layout_list_comment)
    RelativeLayout layoutComment;

    @BindView(R.id.fragment_live_stream_viewer_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fragment_live_stream_viewer_live_stream_share)
    TextView txtShareWall;

    LiveStreamAdapter liveStreamAdapter;

    @Inject
    UserLiveStreamService mUserLiveStreamService;

    /**
     * store selected friends to share
     *
     * @see #onOptionsItemSelected(MenuItem)
     */
    private ArrayList<ListTagFriendsBean> mTaggedFriend = new ArrayList<>();

    private LiveStreamActivityListener listener;

    int startTime = 0;

    Subject<Integer> timerSubject = PublishSubject.create();

    //----------------------------------------------------------------
    //------------------------ Instance ------------------------------
    //----------------------------------------------------------------
    public static LiveStreamViewerFragment newInstance() {
        Bundle args = new Bundle();
        LiveStreamViewerFragment fragment = new LiveStreamViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_stream_viewer;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLiveStreamComponent().inject(this);

        disposables = new CompositeDisposable();

        mKeyboardHeightProvider = new KeyboardHeightProvider(getActivity());

        liveStreamFontBackCamera.setVisibility(View.GONE);
        layoutTimeView.setVisibility(View.VISIBLE);
        mTime.setText(String.format(getResources().getString(R.string.live_streamed_total_time), "00:00"));
        mViewNumber.setText("0");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listComment.setHasFixedSize(true);
        listComment.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listComment.setLayoutManager(mLayoutManager);

        //8dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        listComment.addItemDecoration(new SpacesItemDecoration(space));

        liveStreamAdapter = new LiveStreamAdapter(null);
        liveStreamAdapter.openLoadAnimation(MultifunctionAdapter.SLIDEIN_LEFT);
        liveStreamAdapter.disableLoadMoreIfNeed();

        listComment.setAdapter(liveStreamAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListComment(TAKE_COMMENT);
            }
        });
    }

    @Override
    protected void setUserVisibleHint() {
        listener = (LiveStreamActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(true);

        navigationBar.setVisibility(View.VISIBLE);
        rootView.post(new Runnable() {
            public void run() {
                mKeyboardHeightProvider.start();
            }
        });
        activity = (LiveStreamActivity) context;
        setObservers();
        UserPreferences userPreference = new UserPreferences();
        BuzzDetailRequest buzzDetailRequest = new BuzzDetailRequest(userPreference.getToken(), this.mUserLiveStreamService.buzzId);
        getPresenter().getTimelineDetail(buzzDetailRequest);
        loadListComment(TAKE_COMMENT);

        SystemUtils.hideSoftKeyboard(getActivity());
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.close();
        if (disposables != null)
            disposables.dispose();
    }

    //----------------------------------------------------------------
    //------------------------ click listener ------------------------
    //----------------------------------------------------------------

    @OnClick(R.id.fragment_live_stream_viewer_live_stream_share)
    void onShareWall() {
        mTaggedFriend.clear();
        //Hide soft keyboard if need
        KeyboardUtils.hideSoftKeyboard(activity);
        //Start Tag Friend Activity
//        TagFriendActivity.startActivityForResult(activity, TAG_FRIEND_LIST_FOR_LIVE_STREAM_RESULT, mTaggedFriend, REQUEST_ADD_TAG_FRIEND_FOR_LIVE_STREAM);

        final List<String> listId = new ArrayList<>();
//        Observable.fromIterable(mTaggedFriend).forEach(new Consumer<ListTagFriendsBean>() {
//            @Override
//            public void accept(ListTagFriendsBean tagFriendsFavoriteBean) throws Exception {
//                listId.add(tagFriendsFavoriteBean.userId);
//            }
//        });
        getPresenter().shareMedia(UserPreferences.getInstance().getToken(), "", listId, mUserLiveStreamService.buzzId);

        txtShareWall.setEnabled(false);
    }

    @OnClick(R.id.layout_live_stream_header_close)
    void onCloseLiveStream() {
        getActivity().finish();
    }

    @OnClick(R.id.fragment_live_stream_viewer_send_comment)
    void onSendComment() {
        LogUtils.e(TAG, "Send Comment -----> " + edtComment.getText().toString().trim());
        if (!edtComment.getText().toString().trim().isEmpty()) {

            if (edtComment.length() > Constants.MAX_LENGTH_COMMENT) {
                Toast.makeText(getContext(),
                        String.format(getResources().getString(R.string.validate_comment_limit), String.valueOf(Constants.MAX_LENGTH_COMMENT)),
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                String token = UserPreferences.getInstance().getToken();
                String commentValue = edtComment.getText().toString().trim();
                AddCommentRequest addCommentRequest = new AddCommentRequest(token, mUserLiveStreamService.buzzId, commentValue);
                getPresenter().sendComment(addCommentRequest);
                edtComment.getText().clear();
                hideKeyboard();
            }
        }
    }

    //----------------------------------------------------------------
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------
    private void handleShare(List<ListTagFriendsBean> mTaggedFriend) {

        final List<String> listId = new ArrayList<>();
        Observable.fromIterable(mTaggedFriend).forEach(new Consumer<ListTagFriendsBean>() {
            @Override
            public void accept(ListTagFriendsBean tagFriendsFavoriteBean) throws Exception {
                listId.add(tagFriendsFavoriteBean.userId);
            }
        });
        getPresenter().shareMedia(UserPreferences.getInstance().getToken(), "", listId, mUserLiveStreamService.buzzId);
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height > 100) {
            layoutComment.setVisibility(View.GONE);
            Animation animation = AnimationUtils.outToTopAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isOpen = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    navigationBar.setVisibility(View.GONE);
                    isOpen = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!isOpen)
                navigationBar.startAnimation(animation);
        } else {
            layoutComment.setVisibility(View.VISIBLE);
            navigationBar.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.inFromTopAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isOpen = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    navigationBar.setVisibility(View.VISIBLE);
                    isOpen = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!isOpen)
                navigationBar.startAnimation(animation);
        }
    }

    private void loadListComment(int take) {
        ListCommentRequest listCommentRequest = new ListCommentRequest(UserPreferences.getInstance().getToken(), mUserLiveStreamService.buzzId, 0, take, 1);
        getPresenter().getBuzzListComment(listCommentRequest);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
    }

    private void showKeyboard() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtComment, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    //----------------------------------------------------------------
    //------------------------ Subject Observers ---------------------
    //----------------------------------------------------------------
    private void setObservers() {
        Disposable disposableLiveStreamState = listener.getLiveStreamStateDisposable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LiveStreamActivityListener.LiveStreamState>() {
                    @Override
                    public void accept(LiveStreamActivityListener.LiveStreamState state) throws Exception {
                        if (state == LiveStreamActivityListener.LiveStreamState.JOINED) {
                            progressBar.setVisibility(View.GONE);

                            startTime = mUserLiveStreamService.startTime;
                            timerSubject.onNext(startTime);
                        }
                    }
                });
        disposables.add(disposableLiveStreamState);

        Disposable disposableViewNumber = listener.getViewNumberDisposable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer viewNumber) throws Exception {
                        mViewNumber.setText(viewNumber + "");
                    }
                });
        disposables.add(disposableViewNumber);

        Disposable socketDisposable = listener.getSocketDisposable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ListCommentBean>() {
                    @Override
                    public void accept(ListCommentBean bean) throws Exception {
                        liveStreamAdapter.addData(bean);
                        listComment.smoothScrollToPosition(liveStreamAdapter.getData().size());
                    }
                });
        disposables.add(socketDisposable);

        Disposable disposableTimer = timerSubject.subscribeOn(Schedulers.computation())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void accept(Integer startTime) throws Exception {
                        int seconds = startTime++;
                        int minutes = seconds / 60;
                        int hours = minutes / 60;

                        minutes %= 60;
                        seconds %= 60;
                        hours %= 12;

                        if (minutes > 0) {
                            mTime.setText(String.format(getResources().getString(R.string.live_streamed_total_time), String.format("%02d:%02d", hours, minutes)));
                        } else {
                            mTime.setText(String.format(getResources().getString(R.string.live_streamed_total_time), String.format("%02d:%02d", minutes, seconds)));
                        }

                        timerSubject.onNext(startTime);
                    }
                });
        disposables.add(disposableTimer);
    }

    //----------------------------------------------------------------
    //------------------------ Live Stream Presenter -----------------
    //----------------------------------------------------------------

    @Override
    public void onTimelineDetail(BuzzDetailResponse response) {

        RxEventBus.publish(SubjectCode.SUBJECT_UPDATE_TIMELINE, response.data);

        mUserLiveStreamService.buzzId = response.data.buzzId;
        mUserLiveStreamService.userId = response.data.userId;
        mUserLiveStreamService.roomHash = response.data.listChildBuzzes.get(0).streamId;

        listener.connect();
    }

    @Override
    public void onBuzzListComment(ListCommentResponse response) {
        if (listComment.getVisibility() == View.GONE)
            listComment.setVisibility(View.VISIBLE);
        if (null == response.data) {
            liveStreamAdapter.loadMoreEnd(true);
        } else {
            if (response.data.isEmpty()) {
                liveStreamAdapter.loadMoreEnd(true);
            } else {
                liveStreamAdapter.setNewData(response.data);
            }
        }
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
    }

    @Override
    public void onBuzzNotFoundFromNotificationId() {
        Toast.makeText(getActivity(), R.string.buzz_item_not_found, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void onComplete() {
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
        txtShareWall.setEnabled(true);
    }

    @Override
    public void onRefreshToken(CheckTokenResponse checkTokenResponse) {

    }

    @Override
    public void handleBuzzNotFound(String buzzId) {
        new DialogMaterial.Builder(getActivity())
                .setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.buzz_item_not_found)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void shareMediaFailure() {
        new DialogMaterial.Builder(getActivity())
                .setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.share_live_stream_fail)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void shareMediaSuccess(final PostStatusResponse response) {
        new DialogMaterial.Builder(getActivity())
                .setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.share_live_stream_success)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommentActivity.launch(activity, txtShareWall, response.data.buzzId, mUserLiveStreamService.userId);
                        activity.finish();
                        dialog.dismiss();
                    }
                }).show();
    }

    //----------------------------------------------------------------
    //------------------------ Activity Result -----------------------
    //----------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "resultCode: " + resultCode + "\n" + "requestCode: " + requestCode);

        if (resultCode != Activity.RESULT_OK) return;

        Utils.dumpIntent(data);

        switch (requestCode) {
            case REQUEST_ADD_TAG_FRIEND_FOR_LIVE_STREAM:
                //Get tagged friend list from TagFriendActivity
                mTaggedFriend = data.getParcelableArrayListExtra(TAG_FRIEND_LIST_FOR_LIVE_STREAM_RESULT);
                handleShare(mTaggedFriend);
                break;

            default:
                break;
        }
    }
}

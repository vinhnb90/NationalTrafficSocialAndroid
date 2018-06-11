package com.vn.ntsc.ui.livestream;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseFragment;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.model.token.CheckTokenResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.services.UserLiveStreamService;
import com.vn.ntsc.ui.posts.SharePrivacyActivity;
import com.vn.ntsc.ui.tagfriends.TagFriendActivity;
import com.vn.ntsc.utils.AnimationUtils;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.keyboard.KeyboardHeightObserver;
import com.vn.ntsc.utils.keyboard.KeyboardHeightProvider;
import com.vn.ntsc.utils.keyboard.KeyboardUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.decoration.SpacesItemDecoration;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_ADD_TAG_FRIEND_FOR_LIVE_STREAM;
import static com.vn.ntsc.repository.ActivityResultRequestCode.REQUEST_PRIVACY_FOR_LIVE_STREAM;
import static com.vn.ntsc.ui.livestream.LiveStreamPlayerFragment.UIState.*;

/**
 * Created by nankai on 12/27/2017.
 */

public class LiveStreamPlayerFragment extends BaseFragment<LiveStreamPresenter> implements LiveStreamContract.View, KeyboardHeightObserver {

    private static final String TAG = LiveStreamPlayerFragment.class.getSimpleName();

    //----------------------------------------------------------------
    //------------------------ Variable ------------------------------
    //----------------------------------------------------------------

    private final String TAG_FRIEND_PRIVACY_FOR_LIVE_STREAM_RESULT = "TAG_FRIEND_PRIVACY_FOR_LIVE_STREAM_RESULT";
    private final String TAG_FRIEND_LIST_FOR_LIVE_STREAM_RESULT = "TAG_FRIEND_LIST_FOR_LIVE_STREAM_RESULT";

    private CompositeDisposable disposables;
    private KeyboardHeightProvider mKeyboardHeightProvider;
    private final int TAKE_COMMENT = 50;
    private boolean isOpen;
    private boolean hasStopReadyLiveStreamState = false;
    private NumAnim giftNumAnim;
    private int privacy = 0;// 0 <=> public; 1 <=> friend
    private ArrayList<ListTagFriendsBean> mTaggedFriend = new ArrayList<>();

    @BindView(R.id.layout_live_stream_header_navigation_bar)
    ConstraintLayout navigationBar;
    @BindView(R.id.layout_live_stream_header_layout_time_video)
    LinearLayout layoutTimeView;
    @BindView(R.id.layout_live_stream_header_time)
    TextView mTime;
    @BindView(R.id.layout_live_stream_header_view_number)
    TextViewVectorCompat mViewNumber;
    @BindView(R.id.layout_live_stream_header_font_back_camera)
    ImageView liveStreamFontBackCamera;

    @BindView(R.id.layout_live_stream_comment_layout_list_comment)
    RelativeLayout layoutListComment;
    @BindView(R.id.layout_live_stream_comment_list_comment)
    RecyclerView listComment;
    @BindView(R.id.layout_live_stream_comment_refresh)
    SwipeRefreshLayout refreshLayout;


    @BindView(R.id.fragment_live_stream_player_layout_comment)
    LinearLayout layoutComment;
    @BindView(R.id.fragment_live_stream_player_comment)
    EditText edtComment;
    @BindView(R.id.fragment_live_stream_player_layout_comment_action)
    RelativeLayout layoutCommentAction;
    @BindView(R.id.fragment_live_stream_player_layout_create)
    LinearLayout layoutCreate;
    @BindView(R.id.fragment_live_stream_player_btn_start)
    Button btnStartLiveStream;
    @BindView(R.id.fragment_live_stream_player_description)
    EditText edtDescription;
    @BindView(R.id.fragment_live_stream_player_txt_username)
    TextView mTxtUsername;
    @BindView(R.id.fragment_live_stream_player_private_text)
    TextView mTxtPrivacy;
    @BindView(R.id.fragment_live_stream_player_private_icon)
    ImageView mPrivacyIcon;
    @BindView(R.id.fragment_live_stream_player_img_avatar)
    ImageView mMyAvatar;
    @BindView(R.id.fragment_live_stream_player_layout_create_description)
    LinearLayout layoutEdtCreate;
    @BindView(R.id.fragment_live_stream_player_layout_description_action)
    RelativeLayout layoutDescriptionAction;
    @BindView(R.id.fragment_live_stream_player_state)
    TextView stateView;

    LiveStreamAdapter liveStreamAdapter;

    @Inject
    UserLiveStreamService mUserLiveStreamService;

    @UIState
    int uiState;

    @IntDef({UIState.PREPARE, UIState.READY, UIState.NEW})
    @Retention(RetentionPolicy.RUNTIME)
    @interface UIState {
        int NEW = 0;
        int PREPARE = 1;
        int READY = 2;
        int JOINED = 3;
    }

    private LiveStreamActivityListener listener;

    int startTime = 0;

    Subject<Integer> timerSubject = PublishSubject.create();

    //----------------------------------------------------------------
    //------------------------ Instance ------------------------------
    //----------------------------------------------------------------
    public static LiveStreamPlayerFragment newInstance() {
        Bundle args = new Bundle();
        LiveStreamPlayerFragment fragment = new LiveStreamPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //----------------------------------------------------------------
    //------------------------ life cycle ----------------------------
    //----------------------------------------------------------------
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_stream_player;
    }

    @Override
    protected void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLiveStreamComponent().inject(this);

        mKeyboardHeightProvider = new KeyboardHeightProvider(getActivity());

        giftNumAnim = new NumAnim();

        navigationBar.setVisibility(View.VISIBLE);
        layoutTimeView.setVisibility(View.GONE);
        mTime.setText(String.format(getResources().getString(R.string.live_streamed_total_time), "00:00"));
        mViewNumber.setText("0");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listComment.setHasFixedSize(true);
        listComment.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listComment.setLayoutManager(mLayoutManager);

        //8dp as px, value might be obtained e.g. from dimen resources...
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        listComment.addItemDecoration(new SpacesItemDecoration(space));

        liveStreamAdapter = new LiveStreamAdapter( null);
        liveStreamAdapter.openLoadAnimation(MultifunctionAdapter.SLIDEIN_LEFT);
        liveStreamAdapter.disableLoadMoreIfNeed();

        listComment.setAdapter(liveStreamAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListComment(0, TAKE_COMMENT);
            }
        });

        uiState = UIState.NEW;
        if (mUserLiveStreamService.typeView == UserLiveStreamService.TypeView.STREAM_FILE){
            liveStreamFontBackCamera.setVisibility(View.GONE);
        }else {
            liveStreamFontBackCamera.setVisibility(View.VISIBLE);
        }

        layoutCreate.setVisibility(View.VISIBLE);
        layoutListComment.setVisibility(View.GONE);
        layoutCommentAction.setVisibility(View.GONE);
    }

    @Override
    protected void setUserVisibleHint() {
        rootView.post(new Runnable() {
            public void run() {
                mKeyboardHeightProvider.start();
            }
        });

        disposables = new CompositeDisposable();
        listener = (LiveStreamActivity) context;

        setObservers();

        //Bind user info to view
        bindUserInfo();
        //Set privacy content to post status
        setPrivacyContent();
        edtDescription.clearFocus();
        hideKeyboard(edtDescription);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard(edtComment);
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard(edtComment);
        if (mKeyboardHeightProvider != null)
            mKeyboardHeightProvider.setKeyboardHeightObserver(null);
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

    @OnClick(R.id.layout_live_stream_header_font_back_camera)
    void onFontBackCamera() {
        listener.changeCamera();
    }

    @OnClick(R.id.fragment_live_stream_player_item_open_comment)
    void onDisplayComment() {
        layoutCommentAction.setVisibility(View.GONE);
        layoutComment.setVisibility(View.VISIBLE);
        edtComment.requestFocus();
        showKeyboard(edtComment);
    }

    /**
     * Show when click to show list friend for add tag from tag friend Button
     */
    @OnClick(R.id.fragment_live_stream_player_item_header_tag_friends)
    void onChooseFriend() {
        startTagFriendActivity();
    }

    @OnClick(R.id.fragment_live_stream_player_layout_share_privacy)
    void onSelectPrivacy() {
        //Hide soft keyboard if need
        KeyboardUtils.hideSoftKeyboard(activity);
        //Start choose privacy Activity
        SharePrivacyActivity.startActivityForResult(activity, TAG_FRIEND_PRIVACY_FOR_LIVE_STREAM_RESULT, privacy, REQUEST_PRIVACY_FOR_LIVE_STREAM);
    }

    @OnClick(R.id.fragment_live_stream_player_btn_start)
    void onCreateLiveStream() {
        if (uiState == UIState.NEW) {
            listener.setState(LiveStreamActivityListener.LiveStreamState.PREPARE);
            uiState = UIState.PREPARE;
            // state 3,2,1  live stream prepare, state 4 live ready
            Disposable disposableState = Observable.just(3, 2, 1, 4)
                    .zipWith(Observable.interval(1, TimeUnit.SECONDS), new BiFunction<Integer, Long, Object>() {
                        @Override
                        public Integer apply(Integer integer, Long aLong) throws Exception {
                            return integer;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object integer) throws Exception {
                            int status = Integer.parseInt(integer.toString());
                            if (status == 4) {
                                if (hasStopReadyLiveStreamState) {
                                    resetUiCreateLiveStream();
                                } else {
                                    uiState = UIState.READY;

                                    mUserLiveStreamService.userHash = UserPreferences.getInstance().getToken();
                                    mUserLiveStreamService.buzzVal = edtDescription.getText().toString().trim();
                                    mUserLiveStreamService.privacy = privacy;
                                    List<String> taggedFriendId = new ArrayList<>();
                                    for (ListTagFriendsBean bean : mTaggedFriend)
                                        taggedFriendId.add(bean.userId);
                                    mUserLiveStreamService.tagList = taggedFriendId;

                                    listener.connect();
                                    stateView.setText(getResources().getString(R.string.live_stream_status_start));
                                    edtDescription.setEnabled(false);
                                    btnStartLiveStream.setBackgroundResource(R.drawable.bg_gray_text_radius);
                                    btnStartLiveStream.setText(getResources().getString(R.string.live_stream_text_live_stream));
                                }
                            } else {
                                if (hasStopReadyLiveStreamState) {
                                    if (stateView.getVisibility() == View.VISIBLE)
                                        stateView.setVisibility(View.GONE);
                                    stateView.setText("");
                                    edtDescription.setEnabled(false);
                                    btnStartLiveStream.setBackgroundResource(R.drawable.bg_gray_text_radius);
                                    btnStartLiveStream.setText(getResources().getString(R.string.common_cancel));
                                } else {
                                    if (stateView.getVisibility() == View.GONE)
                                        stateView.setVisibility(View.VISIBLE);
                                    stateView.setText(String.valueOf(status));
                                    edtDescription.setEnabled(false);
                                    giftNumAnim.start(stateView);
                                    btnStartLiveStream.setBackgroundResource(R.drawable.bg_red_text_radius);
                                    btnStartLiveStream.setText(getResources().getString(R.string.common_cancel));
                                }
                            }
                        }
                    });

            disposables.add(disposableState);
        } else if (uiState == UIState.PREPARE) {
            hasStopReadyLiveStreamState = true;
        } else {
            LogUtils.w(TAG, "state live stream : " + uiState);
        }
    }

    @OnClick(R.id.layout_live_stream_header_close)
    void onCloseLiveStream() {
        listener.leave();
    }

    @OnClick(R.id.fragment_live_stream_player_send_comment)
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
                hideKeyboard(edtComment);
            }
        }
    }

    @OnClick(R.id.fragment_live_stream_player_cancel)
    void onCancelLiveStream() {
        listener.leave();
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
                            uiState = UIState.JOINED;
                            updateUiLiveStreamSuccess();
                        } else if (state == LiveStreamActivityListener.LiveStreamState.CLOSE
                                || state == LiveStreamActivityListener.LiveStreamState.ERROR) {
                            resetUiCreateLiveStream();
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
                        if (bean != null) {
                            liveStreamAdapter.addData(bean);
                            listComment.smoothScrollToPosition(liveStreamAdapter.getData().size());
                        }
                    }
                });
        disposables.add(socketDisposable);

        Disposable disposableTimer = timerSubject.subscribeOn(Schedulers.computation())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
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
    //------------------------ Function ------------------------------
    //----------------------------------------------------------------

    private void updateUiLiveStreamSuccess() {

        layoutTimeView.setVisibility(View.VISIBLE);

        if (stateView.getVisibility() == View.VISIBLE)
            stateView.setVisibility(View.GONE);

        stateView.setText("");
        startTime = mUserLiveStreamService.startTime;
        timerSubject.onNext(startTime);

        final Animation inFromBottomAnimationLayoutCommentAction = AnimationUtils.inFromBottomAnimation();
        inFromBottomAnimationLayoutCommentAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutListComment.setVisibility(View.VISIBLE);
                layoutListComment.startAnimation(AnimationUtils.inFromLeftAnimation());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation outToBottomAnimationLayoutDescriptionAction = AnimationUtils.outToBottomAnimation();
        outToBottomAnimationLayoutDescriptionAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutCreate.setVisibility(View.GONE);
                layoutCommentAction.setVisibility(View.VISIBLE);
                layoutCommentAction.startAnimation(inFromBottomAnimationLayoutCommentAction);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layoutCreate.startAnimation(outToBottomAnimationLayoutDescriptionAction);
    }


    private void resetUiCreateLiveStream() {
        listener.setState(LiveStreamActivityListener.LiveStreamState.CLOSE);
        //if the state stop ready live stream, the replace state to close and change stopReadyLiveStreamState = false
        hasStopReadyLiveStreamState = false;
        uiState = UIState.NEW;
        if (stateView.getVisibility() == View.VISIBLE)
            stateView.setVisibility(View.GONE);
        stateView.setText("");
        edtDescription.setEnabled(true);
        btnStartLiveStream.setBackgroundResource(R.drawable.bg_red_text_radius);
        btnStartLiveStream.setText(getResources().getString(R.string.live_stream_button_create_live_stream));
    }

    private void bindUserInfo() {
        //Lazy load avatar bind to view
        ImagesUtils.loadRoundedAvatar(UserPreferences.getInstance().getAva(),UserPreferences.getInstance().getGender(), mMyAvatar);
        //Fill username to view
        if (!Utils.isEmptyOrNull(UserPreferences.getInstance().getUserName())) {
            mTxtUsername.setText(UserPreferences.getInstance().getUserName());
        }
    }

    private void setPrivacyContent() {
        if (privacy == 0) {
            mPrivacyIcon.setImageResource(R.drawable.ic_live_stream_public);
            mTxtPrivacy.setText(getString(R.string.public_privacy));
        } else if (privacy == 1) {
            mPrivacyIcon.setImageResource(R.drawable.ic_live_stream_tag);
            mTxtPrivacy.setText(getString(R.string.friend_privacy));
        } else if (privacy == 2) {
            mPrivacyIcon.setImageResource(R.drawable.ic_privacy_only_me);
            mTxtPrivacy.setText(getString(R.string.onlyme_privacy));
        }
    }

    private void startTagFriendActivity() {
        //Hide soft keyboard if need
        KeyboardUtils.hideSoftKeyboard(activity);
        //Start Tag Friend Activity
        TagFriendActivity.startActivityForResult(activity, TAG_FRIEND_LIST_FOR_LIVE_STREAM_RESULT, mTaggedFriend, REQUEST_ADD_TAG_FRIEND_FOR_LIVE_STREAM);
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height > 100) {
            if (uiState != UIState.JOINED) {
                layoutDescriptionAction.setVisibility(View.GONE);
            } else {
                layoutListComment.setVisibility(View.GONE);
                layoutComment.setVisibility(View.VISIBLE);

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
            }
        } else {
            if (uiState != UIState.JOINED) {
                layoutDescriptionAction.setVisibility(View.VISIBLE);
            } else {
                layoutListComment.setVisibility(View.VISIBLE);
                layoutCommentAction.setVisibility(View.VISIBLE);
                layoutComment.setVisibility(View.GONE);
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
    }

    private void loadListComment(int skip, int take) {
        ListCommentRequest listCommentRequest = new ListCommentRequest(UserPreferences.getInstance().getToken(), mUserLiveStreamService.buzzId, skip, take, 1);
        getPresenter().getBuzzListComment(listCommentRequest);
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(final View view) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    /**
     * Fill all tagged friend into view
     *
     * @param mTaggedFriend the list tagged friend to share new post
     */
    private void fillTaggedInfo(List<ListTagFriendsBean> mTaggedFriend) {
        CharSequence uerName;
        // username click
        if (mTaggedFriend.size() > 1) {
            uerName = Html.fromHtml(String.format(getResources().getString(R.string.tag_friends_live_stream), UserPreferences.getInstance().getUserName(), mTaggedFriend.get(0).userName, (mTaggedFriend.size() - 1) + ""));
        } else if (mTaggedFriend.size() == 1) {
            uerName = Html.fromHtml(String.format(getResources().getString(R.string.tag_friend_live_stream), UserPreferences.getInstance().getUserName(), mTaggedFriend.get(0).userName));
        } else {
            uerName = Html.fromHtml(String.format(getResources().getString(R.string.not_tag_friends_stream), UserPreferences.getInstance().getUserName()));
        }

        SpannableString ss = new SpannableString(uerName);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startTagFriendActivity();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 0, UserPreferences.getInstance().getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(new ForegroundColorSpan(Color.WHITE), 0, UserPreferences.getInstance().getUserName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //display user name vs tag friends
        mTxtUsername.setText(ss);
    }

    //----------------------------------------------------------------
    //------------------------ Live Stream Presenter -----------------
    //----------------------------------------------------------------

    @Override
    public void onTimelineDetail(BuzzDetailResponse response) {

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
    }

    @Override
    public void onRefreshToken(CheckTokenResponse checkTokenResponse) {

    }

    @Override
    public void handleBuzzNotFound(String buzzId) {

    }

    @Override
    public void shareMediaFailure() {

    }

    @Override
    public void shareMediaSuccess(PostStatusResponse response) {

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
                fillTaggedInfo(mTaggedFriend);
                break;
            case REQUEST_PRIVACY_FOR_LIVE_STREAM:
                //Get the privacy option from SharePrivacyActivity
                privacy = data.getIntExtra(TAG_FRIEND_PRIVACY_FOR_LIVE_STREAM_RESULT, 0);
                //Set privacy content to post status
                setPrivacyContent();
                break;
            default:
                break;
        }
    }

    //----------------------------------------------------------------
    //------------------------ Inner class ---------------------------
    //----------------------------------------------------------------

    public class NumAnim {
        private Animator lastAnimator = null;

        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }
}

package com.vn.ntsc.ui.timeline.user.header;

import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nankai.designlayout.BorderView;
import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.image.Image;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.mediadetail.album.AlbumDetailMediaActivity;
import com.vn.ntsc.ui.mediadetail.timeline.TimelineMediaActivity;
import com.vn.ntsc.ui.profile.detail.ProfileDetailActivity;
import com.vn.ntsc.ui.profile.edit.EditProfileActivity;
import com.vn.ntsc.ui.profile.media.myalbum.MyAlbumActivity;
import com.vn.ntsc.ui.profile.media.timeline.TimelineUserTabActivity;
import com.vn.ntsc.ui.profile.media.videoaudio.VideoAudioActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.timeline.adapter.TimelineAdapter;
import com.vn.ntsc.ui.timeline.core.TimelineFragment;
import com.vn.ntsc.ui.timeline.core.TimelineViewHolder;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;
import com.vn.ntsc.widget.views.dialog.DialogEvaluate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by nankai on 8/29/2017.
 */

public class TimelineProfileViewHolder extends TimelineViewHolder<Image> implements DialogEvaluate.OnSaveEvaluateListener {
    private static final String TAG = TimelineProfileViewHolder.class.getSimpleName();

    public interface ProfileListener{
        void setEvaluateUser(int value);
    }

    @BindView(R.id.activity_profile_avatar)
    ImageView imgAvatar;
    @BindView(R.id.activity_profile_background)
    ImageView imgBackground;
    @BindView(R.id.icon_favorite)
    ImageView mFavorite;

    @BindView(R.id.layout_edt)
    LinearLayout layoutEdt;
    @BindView(R.id.layout_favorite)
    LinearLayout layoutFavorite;

    @BindView(R.id.layout_image4)
    RelativeLayout layoutImage4;

    @BindView(R.id.image4)
    ImageView imageView4;
    @BindView(R.id.image3)
    ImageView imageView3;
    @BindView(R.id.image2)
    ImageView imageView2;
    @BindView(R.id.image1)
    ImageView imageView1;
    @BindView(R.id.layout_image_public)
    ConstraintLayout layoutImagesPublic;

    @BindView(R.id.layout_messager)
    LinearLayout mGotoChat;

    @BindView(R.id.num_more)
    TextView numberMore;

    @BindView(R.id.scrim_view)
    View scrimView;

    @BindView(R.id.tv_evaluate)
    TextView mTvEvaluate;

    @BindView(R.id.btn_profile_timeline)
    CardView mCardViewTimeline;
    @BindView(R.id.btn_profile_my_album)
    CardView mCardViewAlbum;
    @BindView(R.id.btn_profile_video_audio)
    CardView getCardVideoAudio;

    @BindArray(R.array.evaluate_voice)
    String[] evaluateVoice;

    @BindArray(R.array.evaluate_color)
    int[] evaluateColor;

    @BindView(R.id.tv_evaluate_1)
    BorderView tvEvaluate1;
    @BindView(R.id.tv_evaluate_2)
    BorderView tvEvaluate2;

    @BindView(R.id.tv_profile_timeline)
    TextView tvProfileTimeline;

    @BindView(R.id.tv_profile_my_album)
    TextView tvProfileMyAlbum;

    @BindView(R.id.tv_profile_video_audio)
    TextView tvProfileVideoAudio;

    private MyProfileActivity activity;

    private List<ListBuzzChild> mImageList = new ArrayList<>();
    private ProfileListener profileListener;

    @Inject
    UserInfoResponse userInfo;

    @Inject
    public TimelineProfileViewHolder(UserInfoResponse infoResponse) {
        this.userInfo = infoResponse;
    }

    public void setActivity(MyProfileActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setFragment(TimelineFragment fragment) {
        super.setFragment(fragment);
    }

    public void setTimelineHeaderProfileViewHolder(ProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_timeline_list_user_images;
    }

    @Override
    public void initInjectView(TimelineAdapter adapter) {
        adapter.setHeaderAndEmpty(true);
        adapter.removeHeaderView(view);
        adapter.addHeaderView(view);
        checkIsMyProfile();
        initUserInfo();

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_UNFAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    if (userInfo.userId.equals(userId.toString())) {
                        userInfo.isFavorite = Constants.BUZZ_TYPE_IS_NOT_FAVORITE;
                        updateFavoriteIcon();
                    }
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_TIMELINE_FAVORITE, this, new Consumer<Object>() {

            @Override
            public void accept(Object userId) throws Exception {
                if (userId != null) {
                    if (userInfo.userId.equals(userId.toString())) {
                        userInfo.isFavorite = Constants.BUZZ_TYPE_IS_FAVORITE;
                        updateFavoriteIcon();
                    }
                }
            }
        });

        RxEventBus.subscribe(SubjectCode.SUBJECT_UPDATE_LIKE_IN_HEADER_PROFILE, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                ListBuzzChild child = (ListBuzzChild) o;
                if (child != null && mImageList != null) {
                    int index = mImageList.indexOf(child);
                    if (index != -1) {
                        mImageList.set(index, child);
                    }
                }
            }
        });
    }

    /*Check whether you are showing your own profile or not
    * if is own profile --> show layoutEdit, hide layoutFavorite*/
    private void checkIsMyProfile() {
        if (!UserPreferences.getInstance().getUserId().isEmpty() && UserPreferences.getInstance().getUserId().equals(userInfo.userId)) {
            layoutEdt.setVisibility(View.VISIBLE);
            mGotoChat.setVisibility(View.GONE);
            layoutFavorite.setVisibility(View.GONE);
            tvProfileMyAlbum.setText(R.string.tab_album_me);

        } else {
            updateFavoriteIcon();
            // Profile is Friends
            layoutEdt.setVisibility(View.GONE);
            mGotoChat.setVisibility(View.VISIBLE);
            layoutFavorite.setVisibility(View.VISIBLE);

            String tabAlbum = activity.getString(R.string.tab_album) + " ";
            Spannable nameSpannable = new SpannableString(tabAlbum + userInfo.userName);
            nameSpannable.setSpan(new StyleSpan(Typeface.BOLD), tabAlbum.length(), nameSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvProfileMyAlbum.setText(nameSpannable);
        }
    }

    public void requestListPublicFileData(String userId, int delay) {
        String token = UserPreferences.getInstance().getToken();
        PublicFileRequest listPublicFileRequest;
        listPublicFileRequest = new PublicFileRequest(0, token, userId, PublicFileRequest.TYPE_ALL);
        getPresenter().getListPublicFile(listPublicFileRequest, userInfo, TypeView.TypeViewTimeline.TIMELINE_USER, delay);
    }

    //get user info
    public void requestGetUserInfo(String userId) {
        if (!UserPreferences.getInstance().getToken().equals(""))
            //get yourself info
            this.activity.getPresenter().getUserInfo(new UserInfoRequest(UserPreferences.getInstance().getToken(), userId), 0);
        else //get friend info
            this.activity.getPresenter().getUserInfo(new UserInfoRequest(userId), 0);
    }

    @OnClick(R.id.tv_evaluate)
    void onClickEvaluate() {
        DialogEvaluate obj = new DialogEvaluate(activity, this);
        obj.show();
    }

    @OnClick(R.id.layout_favorite)
    void onClickFavorite() {
        //update favorite on timeline fragment
        fragment.onFavorite(userInfo);
    }

    @OnClick(R.id.layout_edt)
    void onClickShowEditProfile(View view) {
        EditProfileActivity.launch((AppCompatActivity) fragment.getActivity(), imgAvatar, ActivityResultRequestCode.REQUEST_CODE_EDIT_PROFILE);
    }

    @OnClick(R.id.layout_introduce)
    void onClickShowDetailProfile(View view) {
        ProfileDetailActivity.launch((AppCompatActivity) fragment.getActivity(), userInfo, imgAvatar);
    }

    @OnClick(R.id.activity_profile_avatar)
    void onAvatarClicked(View view) {
        List<MediaEntity> mData = new ArrayList<>();


        mData.add(new MediaEntity(0, userInfo.avatar, TypeView.MediaDetailType.IMAGE_TYPE, userInfo.avatar));
        AlbumDetailMediaActivity.launch(activity, mData, 0);
    }

    @Override
    public void setData(List<Image> datas) {
    }

    public void initUserInfo() {

        if (null != userInfo.avatar) {
            updateAvatar(userInfo.avatar);
        }

        updateFavoriteIcon();

        if (this.userInfo.rateValue != null && this.userInfo.rateValue.length != 0) {
            if (this.userInfo.rateValue.length == 1) {
                tvEvaluate1.setBackgroundColor(evaluateColor[this.userInfo.rateValue[0] - 1]);
                tvEvaluate1.setText(evaluateVoice[this.userInfo.rateValue[0] - 1]);
                tvEvaluate2.setVisibility(View.GONE);
            } else {
                tvEvaluate2.setVisibility(View.VISIBLE);
                tvEvaluate1.setBackgroundColor(evaluateColor[this.userInfo.rateValue[0] - 1]);
                tvEvaluate1.setText(evaluateVoice[this.userInfo.rateValue[0] - 1]);

                tvEvaluate2.setBackgroundColor(evaluateColor[this.userInfo.rateValue[1] - 1]);
                tvEvaluate2.setText(evaluateVoice[this.userInfo.rateValue[1] - 1]);
            }
        }
    }

    public void updateFavoriteIcon() {
        if (userInfo.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE) {
            mFavorite.setImageResource(R.drawable.ic_list_buzz_item_favorited);
        } else {
            mFavorite.setImageResource(R.drawable.ic_list_buzz_item_favorite);
        }
    }

    public void setListPublicImage(List<ListBuzzChild> datas) {
        mImageList.clear();
        mImageList.addAll(datas);
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);
        layoutImage4.setVisibility(View.INVISIBLE);

        if (datas.size() <= 0) {
            layoutImagesPublic.setVisibility(View.GONE);
        } else {
            layoutImagesPublic.setVisibility(View.VISIBLE);
            for (int i = 0; i < datas.size(); i++) {

                if (i == 0) {
                    imageView1.setVisibility(View.VISIBLE);
                    ImagesUtils.loadImageProfile(datas.get(i).thumbnailUrl, imageView1);
                }

                if (i == 1) {
                    imageView2.setVisibility(View.VISIBLE);
                    ImagesUtils.loadImageProfile(datas.get(i).thumbnailUrl, imageView2);
                }

                if (i == 2) {
                    imageView3.setVisibility(View.VISIBLE);
                    ImagesUtils.loadImageProfile(datas.get(i).thumbnailUrl, imageView3);
                }

                if (i == 3) {
                    imageView4.setVisibility(View.VISIBLE);
                    ImagesUtils.loadImageProfile(datas.get(i).thumbnailUrl, imageView4);
                }

                if (i > 3)
                    break;
            }

            if (datas.size() > 4) {
                scrimView.setVisibility(View.VISIBLE);
                layoutImage4.setVisibility(View.VISIBLE);
                numberMore.setVisibility(View.VISIBLE);

                numberMore.setText("+" + (datas.size() - 4));
            } else if (datas.size() == 4) {
                scrimView.setVisibility(View.GONE);
                layoutImage4.setVisibility(View.VISIBLE);
                numberMore.setVisibility(View.GONE);
            }
        }
    }

    public void updateAvatar(String avatar) {
        if (avatar != null) {
            if (userInfo != null) {
                ImagesUtils.loadRoundedAvatar(avatar, userInfo.gender, imgAvatar);
            } else {
                ImagesUtils.loadRoundedAvatar(avatar, UserPreferences.getInstance().getGender(), imgAvatar);
            }

            ImagesUtils.loadBlurImageBanner(fragment.getContext(), AppController.SCREEN_WIDTH, fragment.getResources().getDimensionPixelSize(R.dimen.toolbar_layout_size),
                    avatar, imgBackground);
        }
    }

    private void gotoImageDetail(List<ListBuzzChild> childList, int firstShowPos, View view) {
        TimelineMediaActivity.launch((AppCompatActivity) fragment.getActivity(), view, childList, firstShowPos);
    }

    @OnClick({R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.layout_messager, R.id.btn_profile_timeline, R.id.btn_profile_my_album, R.id.btn_profile_video_audio})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image1:
                gotoImageDetail(mImageList, 0, view);
                break;

            case R.id.image2:
                gotoImageDetail(mImageList, 1, view);
                break;

            case R.id.image3:
                gotoImageDetail(mImageList, 2, view);
                break;

            case R.id.image4:
                gotoImageDetail(mImageList, 3, view);
                break;

            case R.id.layout_messager:
                if (Utils.isEmptyOrNull(UserPreferences.getInstance().getToken())) {
                    fragment.onShowDialogLogin(ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN);
                } else {
                    ChatActivity.newInstance((AppCompatActivity) fragment.getActivity(), userInfo, view);
                }
                break;

            case R.id.btn_profile_timeline:
                TimelineUserTabActivity.startActivity(activity, userInfo.userId, view);
                break;

            case R.id.btn_profile_my_album:
                MyAlbumActivity.startActivity(activity, userInfo.userId, mCardViewAlbum, userInfo.userName);
                break;

            case R.id.btn_profile_video_audio:
                VideoAudioActivity.startActivity(activity, userInfo.userId, view);
                break;
        }
    }

    @Override
    public void onSaveEvaluate(int value) {
        if (profileListener != null) {
            profileListener.setEvaluateUser(value);
        }
    }

    public void onDestroy() {
        activity = null;
        fragment = null;
        mImageList = null;
    }


    public View getAvatarView() {
        return imgAvatar;
    }
}

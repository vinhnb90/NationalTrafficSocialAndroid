package com.vn.ntsc.ui.comment.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.BuzzSubCommentBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.comment.CommentActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.RegionUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.time.TimeUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 10/25/2017.
 */

public class HeaderViewHolder {

    @Nullable
    @BindView(R.id.item_timeline_header_description)
    TextView description;
    @Nullable
    @BindView(R.id.item_timeline_header_more)
    TextView descriptionMore;
    @Nullable
    @BindView(R.id.item_timeline_avatar)
    ImageView avatar;
    @Nullable
    @BindView(R.id.item_timeline_username)
    TextView txtUserName;
    @Nullable
    @BindView(R.id.item_timeline_date_post)
    TextView txtTimePost;
    @Nullable
    @BindView(R.id.item_timeline_icon_status_private)
    ImageView icStatusPrivate;
    @Nullable
    @BindView(R.id.item_timeline_header_event)
    ImageView headerEvent;
    @Nullable
    @BindView(R.id.layout_item_timeline_like_number)
    TextView txtLikeNumber;
    @Nullable
    @BindView(R.id.layout_item_timeline_comment_number)
    TextView txtCommentNumber;
    @Nullable
    @BindView(R.id.layout_item_timeline_share_number)
    TextView txtShareNumber;
    @Nullable
    @BindView(R.id.layout_item_timeline_view_number)
    TextView viewNumber;
    @Nullable
    @BindView(R.id.item_timeline_approve_buzz)
    View approve;

    private BuzzBean bean;
    private int typeView;
    @TimelineType
    private int typeHeader;

    public BuzzBean getData() {
        return bean;
    }

    public int getTypeView() {
        return typeView;
    }

    public int getTypeHeader() {
        return typeHeader;
    }

    public HeaderViewHolder(int typeHeader, View activity, int typeView) {
        ButterKnife.bind(this, activity);
        this.typeView = typeView;
        this.typeHeader = typeHeader;
    }

    public HeaderViewHolder onBindView(final CommentActivity activity, final BuzzBean bean) {

        if (approve != null) {
            if (bean.isApproved == Constants.IS_APPROVED) {
                approve.setVisibility(View.GONE);
            } else {
                approve.setVisibility(View.VISIBLE);
            }
        }

        this.bean = bean;
        if (bean.userId != null) {
            final boolean isOwn = bean.userId.equals(UserPreferences.getInstance().getUserId());

            if (isOwn) {
                headerEvent.setImageResource(R.drawable.ic_buzz_delete);
            } else {
                if (bean.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE) {
                    headerEvent.setImageResource(R.drawable.ic_list_buzz_item_favorited);
                } else {
                    headerEvent.setImageResource(R.drawable.ic_list_buzz_item_favorite);
                }
            }

            if (!Utils.isEmptyOrNull(bean.buzzValue)) {
                description.setVisibility(View.VISIBLE);
                description.setText(bean.buzzValue);
            } else
                description.setVisibility(View.GONE);

            String strCommentNumber = String.format(activity.getResources().getString(R.string.timeline_comment), String.valueOf(bean.getTotalCommentNumber()));

            String strLikeNumber = String.format(activity.getResources().getString(R.string.timeline_like), String.valueOf(bean.like.likeNumber));
            String strShareNumber = String.format(activity.getResources().getString(R.string.timeline_share), String.valueOf(bean.shareNumber));

            SpannableStringBuilder spanTxtTitle = new SpannableStringBuilder();
            spanTxtTitle.append(bean.userName);
            spanTxtTitle.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    //nếu là chủ tài khoản thì không cho bay vào màn profile hoặc nếu đi ra từ timeline thì cũng không cho bay ra profile nữa
                    if (bean.userId.equals(UserPreferences.getInstance().getUserId()) || typeView == TypeView.TypeViewTimeline.TIMELINE_USER) {
                        //TODO someThing
                    } else {
                        MyProfileActivity.launch(activity, widget, bean, ActivityResultRequestCode.REQUEST_BUZZ_PROFILE, TypeView.ProfileType.COME_FROM_TIMELINE);
                    }
                }

            }, 0, spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (typeHeader == TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM) {
                if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                    spanTxtTitle.append(activity.getResources().getString(R.string.timeline_time_live_stream_on));
                } else {
                    spanTxtTitle.append(activity.getResources().getString(R.string.timeline_time_live_stream_off));
                }
            } else if (typeHeader == TimelineType.BUZZ_TYPE_SHARE_AUDIO) {
                spanTxtTitle.append(" ");
                spanTxtTitle.append(Html.fromHtml(activity.getResources().getString(R.string.timeline_time_share_audio)));
                onClickSharedOwner(bean, activity, spanTxtTitle);
            } else if (typeHeader == TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM) {
                spanTxtTitle.append(" ");
                spanTxtTitle.append(Html.fromHtml(activity.getResources().getString(R.string.timeline_time_share_video)));
                onClickSharedOwner(bean, activity, spanTxtTitle);
            }

            onClickTagFriends(bean, activity, spanTxtTitle);

            txtUserName.setText(spanTxtTitle);
            txtUserName.setHighlightColor(Color.TRANSPARENT);
            txtUserName.setMovementMethod(LinkMovementMethod.getInstance());

            txtCommentNumber.setText(strCommentNumber);
            txtLikeNumber.setText(strLikeNumber);
            txtShareNumber.setText(strShareNumber);

            String time;
            try {
                Calendar calendarNow = Calendar.getInstance();

                Calendar calendarSend = Calendar.getInstance(TimeZone.getDefault());
                calendarSend.setTime(TimeUtils.YYYYMMDDHHMMSS.parse(bean.buzzTime));

                time = TimeUtils.getTimelineDif(calendarSend, calendarNow);
            } catch (ParseException e) {
                e.printStackTrace();
                time = activity.getResources().getString(R.string.common_now);
            }

            txtTimePost.setText(String.format(activity.getResources().getString(R.string.timeline_time_location), time, RegionUtils.getInstance(activity).getRegionName(bean.region)));

            icStatusPrivate.setImageResource(R.drawable.ic_public);

            if (!activity.isFinishing()) {
                ImagesUtils.loadRoundedAvatar(bean.avatar, bean.gender, avatar);
            }
            headerEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOwn) {
                        DeleteBuzzRequest deleteBuzzRequest = new DeleteBuzzRequest(UserPreferences.getInstance().getToken(), bean.buzzId);
                        activity.getPresenter().onDelete(deleteBuzzRequest);
                    } else {
                        String token = UserPreferences.getInstance().getToken();
                        if (bean.isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE) {
                            RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(token, bean.userId);
                            activity.getPresenter().onUnFavorite(removeFavoriteRequest);
                        } else {
                            AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token,
                                    bean.userId);
                            activity.getPresenter().onFavorite(addFavoriteRequest);
                        }
                    }
                }
            });

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //nếu là chủ tài khoản thì không cho bay vào màn profile hoặc nếu đi ra từ timeline thì cũng không cho bay ra profile nữa
                    if (bean.userId.equals(UserPreferences.getInstance().getUserId()) || typeView == TypeView.TypeViewTimeline.TIMELINE_USER) {
                        //TODO someThing
                    } else {
                        MyProfileActivity.launch(activity, view, bean, ActivityResultRequestCode.REQUEST_BUZZ_PROFILE, TypeView.ProfileType.COME_FROM_TIMELINE);
                    }
                }
            });
        }
        return HeaderViewHolder.this;
    }

    public void updateFavoriteIcon(int isFavorite, String userId) {
        if (null != userId) {
            final boolean isOwn = userId.equals(UserPreferences.getInstance().getUserId());
            if (isOwn) {
                if (headerEvent != null) {
                    headerEvent.setImageResource(R.drawable.ic_buzz_delete);
                }
            } else {
                if (isFavorite == Constants.BUZZ_TYPE_IS_FAVORITE) {
                    if (headerEvent != null) {
                        headerEvent.setImageResource(R.drawable.ic_list_buzz_item_favorited);
                    }
                } else {
                    if (headerEvent != null) {
                        headerEvent.setImageResource(R.drawable.ic_list_buzz_item_favorite);
                    }
                }
            }
        }
    }

    public void updateShare(Context context) {
        String strShareNumber = String.format(context.getResources().getString(R.string.timeline_share), String.valueOf(++getData().shareNumber));
        if (txtShareNumber != null) {
            txtShareNumber.setText(strShareNumber);
        }
    }

    public void updateCommentNumber(Context context) {

        String strCommentNumber = String.format(context.getString(R.string.timeline_comment), String.valueOf(getData().getTotalCommentNumber()));
        if (txtCommentNumber != null) {
            txtCommentNumber.setText(strCommentNumber);
        }
    }

    public void updateCommentNumber(ListCommentBean bean, Context context) {

        getData().commentNumber = bean.commentNumber;
        getData().subCommentNumber = bean.allSubCommentNumber;

        String strCommentNumber = String.format(context.getString(R.string.timeline_comment), String.valueOf(getData().getTotalCommentNumber()));
        if (txtCommentNumber != null) {
            txtCommentNumber.setText(strCommentNumber);
        }
    }

    public void updateCommentNumber(BuzzSubCommentBean bean, Context context) {

        getData().commentNumber = bean.commentNumber;
        getData().subCommentNumber = bean.allSubCommentNumber;

        String strCommentNumber = String.format(context.getString(R.string.timeline_comment), String.valueOf(getData().getTotalCommentNumber()));
        if (txtCommentNumber != null) {
            txtCommentNumber.setText(strCommentNumber);
        }
    }

    private void onClickSharedOwner(BuzzBean bean, CommentActivity activity, SpannableStringBuilder spanTxtTitle) {
        spanTxtTitle.append(bean.shareDetailBean.userName);
        spanTxtTitle.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                //TODO
            }
        }, spanTxtTitle.length() - bean.shareDetailBean.userName.length(), spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void onClickTagFriends(final BuzzBean bean, final CommentActivity activity, SpannableStringBuilder spanTxtTitle) {
        if (bean.tagNumber >= 1) {
            spanTxtTitle.append(activity.getResources().getString(R.string.timeline_time_with));
            spanTxtTitle.append(bean.tagList.get(0).userName);
            spanTxtTitle.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    //TODO
                }

            }, spanTxtTitle.length() - bean.tagList.get(0).userName.length(), spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (bean.tagNumber > 1) {
            spanTxtTitle.append(activity.getResources().getString(R.string.timeline_time_and));
            String other = String.format(activity.getResources().getString(R.string.timeline_time_other), String.valueOf((bean.tagNumber - 1)));
            spanTxtTitle.append(other);
            spanTxtTitle.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);    // you can use custom color
                    ds.setUnderlineText(false);    // this remove the underline
                }

                @Override
                public void onClick(View widget) {
                    //TODO
                }

            }, spanTxtTitle.length() - other.length(), spanTxtTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void clearReference() {
        bean = null;
    }
}

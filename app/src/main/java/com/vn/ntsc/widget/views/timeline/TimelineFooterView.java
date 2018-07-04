package com.vn.ntsc.widget.views.timeline;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.views.textview.TextViewVectorCompat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineFooterView extends ConstraintLayout {

    @BindView(R.id.layout_item_timeline_view_number)
    AppCompatTextView viewNumber;
    @BindView(R.id.layout_item_timeline_like_number)
    TextViewVectorCompat likeNumber;
    @BindView(R.id.layout_item_timeline_comment_number)
    TextViewVectorCompat commentNumber;
    @BindView(R.id.layout_item_timeline_share_number)
    TextViewVectorCompat shareNumber;

    FooterListener listener;

    public interface FooterListener {


        void onShare(BuzzBean bean, View view);

        void onShowComment(BuzzBean bean, View view);

        void onLike(BuzzBean bean, View view);
    }

    public TimelineFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TimelineFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_timeline_footer, this, true);
        this.setId(R.id.item_timeline_footer);
        ButterKnife.bind(this);
    }

    public void setListener(FooterListener listener) {
        if (this.listener == null)
            this.listener = listener;
    }

    public void setViewNumber(ListBuzzChild bean) {
        viewNumber.setText(String.format(getResources().getString(R.string.timeline_time_view), bean.viewNumber));
    }

    public void setVisibleViewNumber(boolean isShowing) {
        viewNumber.setVisibility(isShowing ? VISIBLE : GONE);
    }

    public void setLikeNumber(final BuzzBean bean) {
        String strLikeNumber = String.format(getResources().getString(R.string.timeline_like), Utils.format(bean.like.likeNumber));
        likeNumber.setText(strLikeNumber);
        likeNumber.setOnClickListener(null);
        likeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onLike(bean, view);
            }
        });
    }

    public void setCommentNumber(final BuzzBean bean) {
        String strCommentNumber = String.format(getResources().getString(R.string.timeline_comment), Utils.format(bean.getTotalCommentNumber()));  //Comment number
        commentNumber.setText(strCommentNumber);
        commentNumber.setOnClickListener(null);
        commentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onShowComment(bean, view);
            }
        });
    }

    public void setShareNumber(final BuzzBean bean) {
        String strShareNumber = String.format(getResources().getString(R.string.timeline_share), Utils.format(bean.shareNumber));
        shareNumber.setText(strShareNumber);
        shareNumber.setOnClickListener(null);
        shareNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onShare(bean, view);
            }
        });
    }

    public void setIconLke(@DrawableRes int idRes) {
        likeNumber.setVectorDrawableLeft(idRes);
    }

    public void setIconShare(@DrawableRes int idRes) {
        shareNumber.setVectorDrawableLeft(idRes);
    }

    public void setIconComment(@DrawableRes int idRes) {
        commentNumber.setVectorDrawableLeft(idRes);
    }
    
    public void setLikeNumber(final String strLike) {
        likeNumber.setText(strLike);
    }

    public void setCommentNumber(final String strComment) {
        commentNumber.setText(strComment);
    }

    public void setShareNumber(final String strShare) {
        shareNumber.setText(strShare);
    }
}

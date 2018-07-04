package com.vn.ntsc.ui.timeline.livestream;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.adapter.BaseViewHolder;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;
import com.vn.ntsc.widget.views.images.CircleImageView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nankai on 8/10/2017.
 */

public class TimelineLiveStreamAdapter extends MultifunctionAdapter<TimelineLiveStreamAdapter.ViewHolder, BuzzBean> {

    public TimelineLiveStreamAdapter(LiveStreamListener<BuzzBean> listener) {
        super(listener);
    }

    @Override
    protected ViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timeline_list_live_stream, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onViewReady(ViewHolder helper, BuzzBean item, int position) {
        if (mData.size() > 0)
            helper.onBinView(item, position, (LiveStreamListener<BuzzBean>) listener);
    }

    static class ViewHolder extends BaseViewHolder {

        @Nullable
        @BindView(R.id.avatar_live_stream)
        CircleImageView avatarView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBinView(final BuzzBean bean, final int position, final LiveStreamListener<BuzzBean> listener) {

            if (bean == null || bean.listChildBuzzes == null || bean.listChildBuzzes.isEmpty() || bean.listChildBuzzes.get(0).streamStatus == null)
                return;

            if (bean.listChildBuzzes.get(0).streamStatus.equals(Constants.LIVE_STREAM_ON)) {
                Objects.requireNonNull(avatarView).setBorderColor(itemView.getContext().getResources().getColor(R.color.default_app));
            } else {
                Objects.requireNonNull(avatarView).setBorderColor(itemView.getContext().getResources().getColor(R.color.gray));
            }

            ImagesUtils.loadRoundedAvatar(bean.avatar, bean.gender, false, avatarView);

            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemLiveStreamListener(bean, position, v);
                }
            });
        }
    }
}

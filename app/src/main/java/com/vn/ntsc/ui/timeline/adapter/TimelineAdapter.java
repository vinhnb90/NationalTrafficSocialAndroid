package com.vn.ntsc.ui.timeline.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.vn.ntsc.R;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.TimelineType;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.ui.timeline.core.TimelineListener;
import com.vn.ntsc.ui.timeline.viewholder.BaseTimelineViewHolder;
import com.vn.ntsc.ui.timeline.viewholder.LiveStreamTimelineViewHolder;
import com.vn.ntsc.ui.timeline.viewholder.MediaTimelineViewHolderFiveBuzz;
import com.vn.ntsc.ui.timeline.viewholder.MediaTimelineViewHolderFourBuzz;
import com.vn.ntsc.ui.timeline.viewholder.MediaTimelineViewHolderMoreBuzz;
import com.vn.ntsc.ui.timeline.viewholder.MediaTimelineViewHolderOneBuzz;
import com.vn.ntsc.ui.timeline.viewholder.MediaTimelineViewHolderThreeBuzz;
import com.vn.ntsc.ui.timeline.viewholder.MediaTimelineViewHolderTwoBuzz;
import com.vn.ntsc.ui.timeline.viewholder.ShareLiveStreamTimelineViewHolder;
import com.vn.ntsc.ui.timeline.viewholder.ShareTimelineViewHolder;
import com.vn.ntsc.ui.timeline.viewholder.StatusBaseTimelineViewHolder;
import com.vn.ntsc.ui.timeline.viewholder.VideoAudioTimelineViewHolder;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.adapter.MultifunctionAdapter;

/**
 * Created by nankai on 8/8/2017.
 */

public class TimelineAdapter<E extends BuzzBean> extends MultifunctionAdapter<BaseTimelineViewHolder, E> {

    private LayoutInflater inflater;
    private RequestManager glide;

    /**
     * @param listener Event item Click "listener extends BaseAdapterCallback"
     */
    public TimelineAdapter(RequestManager glide, LayoutInflater inflater, TimelineListener<E> listener) {
        super(listener);
        this.inflater = inflater;
        this.glide = glide;
    }

    public void removeTemplate(String templateId) {
        for (int position = 0; position < mData.size(); position++) {
            E item = mData.get(position);
            if (item != null)
                if (item.buzzId.equals(templateId)) {
                    mData.remove(position);
                    notifyDataSetChanged();
                    break;
                }
        }
    }

    public void updateTemplate(E data, String templateId) {
        LogUtils.e(TAG, "updateTemplate --> " + templateId);
        boolean isExistTemplateId = false;
        for (int position = 0; position < mData.size(); position++) {
            E item = mData.get(position);
            if (item != null)
                if (item.buzzId.equals(templateId)) {
                    isExistTemplateId = true;
                    getData().set(position, data);
                    notifyItemChanged(position + getHeaderLayoutCount());
                    break;
                }
        }
        if (!isExistTemplateId) {
            addData(0, data);
        }
    }

    public void updateErrorRequestBuzzDetail(BuzzDetailRequest listDetailRequest, String templateId, boolean isError) {
        for (int position = 0; position < mData.size(); position++) {
            E item = mData.get(position);
            if (item != null)
                if (item.buzzId.equals(templateId)) {
                    item.isError = isError;
                    item.buzzDetailRequest = listDetailRequest;
                    getData().set(position, item);
                    notifyItemChanged(position + getHeaderLayoutCount());
                }
        }
    }

    //Set type view
    @Override
    protected int onInjectItemViewType(int position) {
        if (mData.get(position).isTemplate()) { //Layout template
            int childNumber = mData.get(position).childNumber;
            if (childNumber == 1) {
                switch (mData.get(position).listChildBuzzes.get(0).buzzType) {
                    case Constants.BUZZ_TYPE_FILE_VIDEO:
                        return TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1_TEMPLATE;
                    case Constants.BUZZ_TYPE_FILE_AUDIO:
                        return TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO_TEMPLATE;
                    default:
                        return TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1_TEMPLATE;
                }
            } else if (childNumber == 2) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_2_TEMPLATE;
            } else if (childNumber == 3) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_3_TEMPLATE;
            } else if (childNumber == 4) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_4_TEMPLATE;
            } else if (childNumber == 5) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_5_TEMPLATE;
            } else if (childNumber > 5) {
                return TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE_TEMPLATE;
            } else {
                return TimelineType.BUZZ_TYPE_STATUS_TEMPLATE;
            }
        } else {
            if (mData.get(position).isBuzzShare()) {
                if (mData.get(position).shareDetailBean.listChildBuzzes.get(0).buzzType.equals(Constants.BUZZ_TYPE_FILE_LIVE_STREAM))
                    return TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM;
                return TimelineType.BUZZ_TYPE_SHARE_AUDIO;
            } else {
                int childNumber = mData.get(position).childNumber;
                if (childNumber == 1) {
                    switch (mData.get(position).listChildBuzzes.get(0).buzzType) {
                        case Constants.BUZZ_TYPE_FILE_VIDEO:
                            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1;
                        case Constants.BUZZ_TYPE_FILE_LIVE_STREAM:
                            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM;
                        case Constants.BUZZ_TYPE_FILE_AUDIO:
                            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO;
                        default:
                            return TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1;
                    }
                } else if (childNumber == 2) {
                    return TimelineType.BUZZ_TYPE_MULTI_BUZZ_2;
                } else if (childNumber == 3) {
                    return TimelineType.BUZZ_TYPE_MULTI_BUZZ_3;
                } else if (childNumber == 4) {
                    return TimelineType.BUZZ_TYPE_MULTI_BUZZ_4;
                } else if (childNumber == 5) {
                    return TimelineType.BUZZ_TYPE_MULTI_BUZZ_5;
                } else if (childNumber > 5) {
                    return TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE;
                } else {
                    return TimelineType.BUZZ_TYPE_STATUS;
                }
            }
        }
    }

    //create view holder
    @Override
    protected BaseTimelineViewHolder onInjectViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            //Buzz share
            case TimelineType.BUZZ_TYPE_SHARE_AUDIO:
                return new ShareTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_share_audio, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_SHARE_LIVE_STREAM:
                return new ShareLiveStreamTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_share_live_stream, parent, false), viewType)
                        .initGlide(glide);
            //Buzz normal
            case TimelineType.BUZZ_TYPE_STATUS:
                return new StatusBaseTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_status, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1:
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO:
                return new VideoAudioTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_video_1, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_LIVE_STREAM:
                return new LiveStreamTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_live_stream_1, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1:
                return new MediaTimelineViewHolderOneBuzz(inflater.inflate(R.layout.layout_item_timeline_image_1, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_2:
                return new MediaTimelineViewHolderTwoBuzz(inflater.inflate(R.layout.layout_item_timeline_image_2, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_3:
                return new MediaTimelineViewHolderThreeBuzz(inflater.inflate(R.layout.layout_item_timeline_image_3, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_4:
                return new MediaTimelineViewHolderFourBuzz(inflater.inflate(R.layout.layout_item_timeline_image_4, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_5:
                return new MediaTimelineViewHolderFiveBuzz(inflater.inflate(R.layout.layout_item_timeline_image_5, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE:
                return new MediaTimelineViewHolderMoreBuzz(inflater.inflate(R.layout.layout_item_timeline_image_more, parent, false), viewType)
                        .initGlide(glide);
            // Buzz template
            case TimelineType.BUZZ_TYPE_STATUS_TEMPLATE:
                return new StatusBaseTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_status_template, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_VIDEO_1_TEMPLATE:
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_AUDIO_TEMPLATE:
                return new VideoAudioTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_video_1_template, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_IMAGE_1_TEMPLATE:
                return new MediaTimelineViewHolderOneBuzz(inflater.inflate(R.layout.layout_item_timeline_image_1_template, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_2_TEMPLATE:
                return new MediaTimelineViewHolderTwoBuzz(inflater.inflate(R.layout.layout_item_timeline_image_2_template, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_3_TEMPLATE:
                return new MediaTimelineViewHolderThreeBuzz(inflater.inflate(R.layout.layout_item_timeline_image_3_template, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_4_TEMPLATE:
                return new MediaTimelineViewHolderFourBuzz(inflater.inflate(R.layout.layout_item_timeline_image_4_template, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_5_TEMPLATE:
                return new MediaTimelineViewHolderFiveBuzz(inflater.inflate(R.layout.layout_item_timeline_image_5_template, parent, false), viewType)
                        .initGlide(glide);
            case TimelineType.BUZZ_TYPE_MULTI_BUZZ_MORE_TEMPLATE:
                return new MediaTimelineViewHolderMoreBuzz(inflater.inflate(R.layout.layout_item_timeline_image_more_template, parent, false), viewType)
                        .initGlide(glide);
            default:
                return new StatusBaseTimelineViewHolder(inflater.inflate(R.layout.layout_item_timeline_status_template, parent, false), viewType)
                        .initGlide(glide);
        }
    }

    //View on ready
    @Override
    protected void onViewReady(BaseTimelineViewHolder helper, E item, int position) {
        if (!mData.isEmpty()) {
            helper.initBindView(item.isTemplate(), item, position, (TimelineListener) listener);
        }
    }
}

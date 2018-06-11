package com.vn.ntsc.ui.mediadetail.base;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.widget.views.images.ImageViewTouch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThoNh on 11/21/2017.
 */

public class MediaAdapter extends PagerAdapter {

    public interface OnImageTouchListener {

        void onImageTouchDown(float originViewY, float eventRawY);

        void onImageTouchUp(float getY, float rawY, ImageViewTouch imageView);

        void onImageTouchMove(float rawY, ImageViewTouch imageView);
    }

    public interface OnPageChangeListener {

        void onPageComing();

        void onPageLeaving();
    }

    public void removeItem(AppCompatActivity activity, int position) {
        if (mData.size() == 1) { // the last item -->  finish
            mData.get(0).onActivityDestroy();
            activity.finish();
        }
        mData.remove(position);
        notifyDataSetChanged();
    }

    public List<MediaEntity> mData;
    private OnImageTouchListener mListener;


    public MediaAdapter(List<MediaEntity> data, OnImageTouchListener listener) {
        mData = new ArrayList<>();
        this.mData = data;
        this.mListener = listener;
    }

    /*Using the LayoutInflater, you can inflate any desired XML layout.*/
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return instantiateItemLayout(container, position);
    }

    /* This method removes a particular view from the collection of Views maintained by the PagerAdapter*/
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    /* return the number of views that will be maintained by the ViewPager.*/
    @Override
    public int getCount() {
        return mData.size();
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private Object instantiateItemLayout(final ViewGroup container, final int position) {
        View layout;
        LayoutInflater mLayoutInflater = LayoutInflater.from(container.getContext());
        switch (mData.get(position).mType) {

            case TypeView.MediaDetailType.IMAGE_TYPE:
                layout = mLayoutInflater.inflate(R.layout.item_media_detail_image, container, false);
                final ImageViewTouch imageView = layout.findViewById(R.id.image_view);

                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        ImagesUtils.loadImage(mData.get(position).mThumbnail, imageView);
                    }
                });

                imageView.setOnTouchImageViewListener(new ImageViewTouch.OnTouchImageViewListener() {
                    @Override
                    public void onDown(float originViewY, float eventRawY) {
                        mListener.onImageTouchDown(originViewY, eventRawY);
                    }

                    @Override
                    public void onUp(float getY, float rawY) {
                        mListener.onImageTouchUp(getY, rawY, imageView);
                    }

                    @Override
                    public void onMove(float rawY) {
                        mListener.onImageTouchMove(rawY, imageView);
                    }
                });
                break;

            case TypeView.MediaDetailType.VIDEO_TYPE:
                layout = mLayoutInflater.inflate(R.layout.item_media_detail_video, container, false);
                new Video(container.getContext(), mData.get(position), layout);
                break;


            case TypeView.MediaDetailType.STREAM_TYPE:
                layout = mLayoutInflater.inflate(R.layout.item_media_detail_video, container, false);
                new Video(container.getContext(), mData.get(position), layout);
                break;

            case TypeView.MediaDetailType.AUDIO_TYPE:
                layout = mLayoutInflater.inflate(R.layout.item_media_detail_audio, container, false);
                new Audio(container.getContext(), mData.get(position), layout);
                break;
            default:
                layout = mLayoutInflater.inflate(R.layout.layout_null, container, false);
        }
        container.addView(layout);
        return layout;
    }

}

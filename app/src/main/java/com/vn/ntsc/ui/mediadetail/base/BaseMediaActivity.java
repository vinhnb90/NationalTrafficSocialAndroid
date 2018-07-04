package com.vn.ntsc.ui.mediadetail.base;

import android.support.annotation.IntRange;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.LogUtils;

import java.util.List;

public abstract class BaseMediaActivity<T extends BasePresenter> extends BaseActivity<T>
        implements ViewPager.OnPageChangeListener {

    /*-----------------------------------------------var-----------------------------------------------*/
    private static final String TAG = BaseMediaActivity.class.getSimpleName();

    //builder pattern for setup viewpager
    private List<MediaEntity> mEntities;
    private ViewPagerMedia mViewPager;
    private int mFirstIndex;
    private int mPageLimit = 4;
    private View viewContainer;
    private ViewPagerMedia.IDoTaskWhenTouch mIDoTaskWhenTouch;
    private ViewPagerMedia.IOnMediaIteractor mIOnMediaIteractor;
    private ViewPagerMedia.IOnMediaTouchListener mIOnMediaTouchListener;


    private MediaAdapter mAdapter;

    private int mLastPosition;

    /*-----------------------------------------------instance-----------------------------------------------*/

    /**
     * set adapter for viewpager
     *
     * @param viewPagerBuilder config
     */
    private BaseMediaActivity<T> setupViewPager(final ViewPagerBuilder viewPagerBuilder) {

        this.mEntities = viewPagerBuilder.entities;
        this.mViewPager = viewPagerBuilder.viewPager;
        this.mViewPager.setIDoTaskWhenTouch(viewPagerBuilder.iDoTaskWhenTouch);
        this.mFirstIndex = viewPagerBuilder.firstIndex;
        this.mPageLimit = viewPagerBuilder.pageLimit;
        this.viewContainer = viewPagerBuilder.viewContainer;
        this.mIDoTaskWhenTouch = viewPagerBuilder.iDoTaskWhenTouch;
        this.mIOnMediaIteractor = viewPagerBuilder.iOnMediaIteractor;
        this.mIOnMediaTouchListener = viewPagerBuilder.iOnMediaTouchListener;


        //setup
        this.mViewPager.setViewContainer(this.viewContainer);
        this.mLastPosition = this.mFirstIndex;
        mAdapter = new MediaAdapter(getSupportFragmentManager(), this.mEntities);
        mViewPager.setOffscreenPageLimit(this.mPageLimit);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(this.mFirstIndex);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                onPageSelected(mFirstIndex);
            }
        });

        return this;
    }

    /*-----------------------------------------------lifecycle-----------------------------------------------*/
    @Override
    public void onViewReady() {
    }

    @Override
    public void onPageSelected(int position) {
        LogUtils.e(TAG, "onPageSelected(" + position + ")");
        if (mEntities.size() == 0) {
            finish();
            return;
        }

        mLastPosition = position;
        LogUtils.e(TAG, "onPageSelected ---> " + "mLastPosition:" + mLastPosition + "-position:" + position);
    }

    /*-----------------------------------------------override-----------------------------------------------*/

    /*-----------------------------------------------func-----------------------------------------------*/

    public void removeItem(int position) {
        mAdapter.removeItem(position);
    }

    public int getCurrentPositionMedia() {
        return mViewPager.getCurrentItem();
    }


    public MediaEntity getCurrentMedia() {
        return mEntities.get(getCurrentPositionMedia());
    }

    public void orientationChange(int orient) {

    }
    /*-----------------------------------------------interface-----------------------------------------------*/

    public class ViewPagerBuilder {
        final List<MediaEntity> entities;
        final ViewPagerMedia viewPager;
        final int firstIndex;
        int pageLimit;
        View viewContainer;
        ViewPagerMedia.IDoTaskWhenTouch iDoTaskWhenTouch;
        ViewPagerMedia.IOnMediaIteractor iOnMediaIteractor;
        ViewPagerMedia.IOnMediaTouchListener iOnMediaTouchListener;

        public ViewPagerBuilder(List<MediaEntity> entities, ViewPagerMedia viewPager, int firstIndex) {
            this.entities = entities;
            this.viewPager = viewPager;
            this.firstIndex = firstIndex;
        }

        public ViewPagerBuilder setViewContainer(View viewContainer) {
            this.viewContainer = viewContainer;
            return this;
        }

        public ViewPagerBuilder setiDoTaskWhenTouch(ViewPagerMedia.IDoTaskWhenTouch iDoTaskWhenTouch) {
            this.iDoTaskWhenTouch = iDoTaskWhenTouch;
            return this;
        }

        public ViewPagerBuilder setiOnMediaIteractor(ViewPagerMedia.IOnMediaIteractor iOnMediaIteractor) {
            this.iOnMediaIteractor = iOnMediaIteractor;
            return this;
        }

        public ViewPagerBuilder setiOnMediaTouchListener(ViewPagerMedia.IOnMediaTouchListener iOnMediaTouchListener) {
            this.iOnMediaTouchListener = iOnMediaTouchListener;
            return this;
        }

        public void setPageLimit(@IntRange(from = 3) int pageLimit) {
            this.pageLimit = pageLimit;
        }

        public BaseMediaActivity<T> build() {
            return setupViewPager(ViewPagerBuilder.this);
        }
    }
}

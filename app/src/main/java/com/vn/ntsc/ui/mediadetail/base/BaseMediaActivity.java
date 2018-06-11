package com.vn.ntsc.ui.mediadetail.base;

import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.media.MediaEntity;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.views.images.ImageViewTouch;

import java.util.List;

/**
 * Created by ThoNh on 11/21/2017.
 */

public abstract class BaseMediaActivity<T extends BasePresenter> extends BaseActivity<T>
        implements ViewPager.OnPageChangeListener, MediaAdapter.OnImageTouchListener, SensorEventListener {
    private static final String TAG = BaseMediaActivity.class.getSimpleName();

    private List<MediaEntity> mEntities;
    private ViewPager mViewPager;
    private MediaAdapter mAdapter;
    private int mLastPosition;

    private static final int THRESHOLD = 400;       /*Threshold for finish activity*/
    private float mOriginY;                         /*Origin position when user touch down screen*/
    private float mDy;                              /*Distance from Top of View to point of Touch*/
    private float mDeltaY;                          /*Distance from mOriginY to Point TouchUp*/

    private int mOrientation = 0;
    private boolean isOrientationSettingOn;
    private SensorManager sensorManager;

    @Override
    public void onViewReady() {
        getContentResolver().registerContentObserver(Settings.System.getUriFor
                        (Settings.System.ACCELEROMETER_ROTATION),
                true, rotationObserver);
    }

    @Override
    public void onResume(View viewRoot) {
        super.onResume(viewRoot);

//        startOrientationSensor();

        if (mViewPager != null && getCurrentMedia() != null)
            getCurrentMedia().onActivityResume();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        isOrientationSettingOn = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mEntities != null && mEntities.size() != 0 && getCurrentMedia() != null)
            getCurrentMedia().onActivityPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mEntities != null && mEntities.size() != 0 && getCurrentMedia() != null)
            getCurrentMedia().onActivityStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // release all media

        if (mAdapter != null && mAdapter.mData != null) {
            for (MediaEntity e : mAdapter.mData) {
                e.onActivityDestroy();
            }
        }

        // avoid leak content observer
        getContentResolver().unregisterContentObserver(rotationObserver);
        rotationObserver = null;

        // kill process media
//        Process.killProcess(Process.myPid());
    }

    private ContentObserver rotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            isOrientationSettingOn = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
            LogUtils.e(TAG, "--------------> mAutoRotateSettingChangeReceiver" + isOrientationSettingOn);
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        LogUtils.e(TAG, "onPageScrolled(" + position + ", " + positionOffset + ", " + positionOffsetPixels + ")");
    }

    @Override
    public void onPageSelected(int position) {
        LogUtils.e(TAG, "onPageSelected(" + position + ")");
        if (mEntities.size() == 0) {
            finish();
            return;
        }

        try {
            if (mLastPosition != position) {
                LogUtils.e(TAG, "onPageSelected ---> " + "mLastPosition:" + mLastPosition + "-position:" + position);

                if (mLastPosition < mEntities.size() -1)
                mEntities.get(mLastPosition).onPageLeaving(mLastPosition);

                mEntities.get(position).setPlayImmediately(true);
                mEntities.get(position).onPageComing(position);
                mLastPosition = position;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        LogUtils.e(TAG, "onPageScrollStateChanged ---> " + state);
    }


    @Override
    public void onImageTouchDown(float originViewY, float eventRawY) {
        // to restore
        mOriginY = originViewY;
        // calculate distance from top of view to top of touch point
        mDy = mOriginY - eventRawY;
    }

    @Override
    public void onImageTouchUp(float getY, float rawY, ImageViewTouch imageView) {
        if (Math.round(Math.abs(mDeltaY)) > THRESHOLD) {
            finish();
        } else {
            animateAlphaFinish1F();
            imageView.animate().y(mOriginY).setDuration(0).start();// move content
        }
    }

    @Override
    public void onImageTouchMove(float rawY, ImageViewTouch imageView) {
        float viewY = rawY + mDy;
        mDeltaY = Math.round(Math.abs(viewY - mOriginY));
        imageView.animate().y(viewY).setDuration(0).start();  // move content

        float percent = calculateViewOpacity();
        animateAlphaWhenSwipeImage(percent);
    }

    /**
     * @return opacity of container depend on screen height
     */
    private float calculateViewOpacity() {
        return 1 - (Math.abs(mDeltaY) / (AppController.SCREEN_HEIGHT / 2));
    }

    /**
     * If media is Image, U can Swipe Image to Top or Bottom for finish Activity like Zalo
     * If swipe to Top or Bottom out of {BaseMediaActivity.THRESHOLD} this Activity will be finish
     * While u swipe this method will be called, and method return percent alpha corresponding with distance
     * U can set alpha for some View u want
     *
     * @param percent percent alpha corresponding with distance swipe
     */
    public void animateAlphaWhenSwipeImage(float percent) {

    }

    /**
     * After swipe but user don't swipe out of Threshold or user don't want finish activity, this method will be call
     * May be u want restore state alpha some view
     */
    public void animateAlphaFinish1F() {

    }

    /**
     * set adapter for viewpager
     *
     * @param viewPager
     * @param mediaEntities data for adapter
     */
    public void setupViewPager(ViewPager viewPager, List<MediaEntity> mediaEntities, final int firstIndex) {
        this.mLastPosition = firstIndex;
        this.mViewPager = viewPager;
        this.mEntities = mediaEntities;
        mEntities.get(firstIndex).setPlayImmediately(true);
        mAdapter = new MediaAdapter(mediaEntities, this);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(firstIndex);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
               onPageSelected(firstIndex);
            }
        });
    }


    public int getCurrentPositionMedia() {
        return mViewPager.getCurrentItem();
    }


    public MediaEntity getCurrentMedia() {
        return mEntities.get(getCurrentPositionMedia());
    }

    public void removeItem(int position) {
        mAdapter.removeItem(this, position);
    }


    /**
     * Listening via SensorEventListener interface
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        int newOrientation = mOrientation;
        if (x < 5 && x > -5 && y > 5)
            newOrientation = 0;
        else if (x < -5 && y < 5 && y > -5)
            newOrientation = 90;
        else if (x < 5 && x > -5 && y < -5)
            newOrientation = 180;
        else if (x > 5 && y < 5 && y > -5)
            newOrientation = 270;

        if (mOrientation != newOrientation) {
            mOrientation = newOrientation;
            Log.e(TAG, "--->mOrientation=" + mOrientation + "--> can change orientation");
            getCurrentMedia().onActivityConfigChange(mOrientation, isOrientationSettingOn);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Call from {@link Video} when orientation change
     *
     * @param orient {Configuration.ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_PORTRAIT}
     */
    public void orientationChange(int orient) {

    }

    /**
     * Register a sensor to track phoneOrientation changes-> Use to change Orientation of device
     */

//    private OrientationEventListener mOrientationHelper;
//    private int mAlwaysChangingPhoneAngle = -1;

//    private synchronized void startOrientationSensor() {//<<<<<<<<<<-------------------- Call on Resume
//        if (mOrientationHelper == null) {
//            mOrientationHelper = new LocalOrientationEventListener(this);
//        }
//        mOrientationHelper.enable();
//    }
//
//    private class LocalOrientationEventListener extends OrientationEventListener {
//        public LocalOrientationEventListener(Context context) {
//            super(context);
//        }
//
//        @Override
//        public void onOrientationChanged(final int o) {
//            if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
//                return;
//            }
//
//            int degrees = 270;
//            if (o < 45 || o > 315)
//                degrees = 0;
//            else if (o < 135)
//                degrees = 90;
//            else if (o < 225)
//                degrees = 180;
//
//            if (mAlwaysChangingPhoneAngle == degrees) {
//                return;
//            }
//            mAlwaysChangingPhoneAngle = degrees;
//
//            Log.d(TAG, "Phone orientation changed to degrees =" + degrees);
//            int rotation = (360 - degrees) % 360;
//
//            //getCurrentMedia().onActivityConfigChange(rotation);
//        }
//    }

}

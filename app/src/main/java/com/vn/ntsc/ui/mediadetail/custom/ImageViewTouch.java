package com.vn.ntsc.ui.mediadetail.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;

public class ImageViewTouch extends ImageViewTouchBase {

    /**
     * var
     */
    //min zoom to begin actin auto zoom origin
    static final float MIN_ZOOM = 0.9f;
    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;
    protected GestureListener mGestureListener;
    protected ScaleListener mScaleListener;

    //get touch slop = max distance move x can detect action swipe view pager
    protected int mTouchSlop;

    //scale current
    protected float mCurrentScaleFactor;
    //scale target
    protected float mScaleFactor;

    //flag detect double tap action presenter
    protected int mDoubleTapDirection;


    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(IteractorGestureListenerA callback) {
        super.init(callback);
        //get touch slop
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        //listener detect gesture scroll, double tap, fling
        mGestureListener = new GestureListener(callback);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);

        //listener detect scale
        mScaleListener = new ScaleListener();
        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);

        //init
        mCurrentScaleFactor = 1f;
        mDoubleTapDirection = 1;
    }

    @Override
    public void setImageRotateBitmapReset(RotateBitmap bitmap, boolean reset) {
        super.setImageRotateBitmapReset(bitmap, reset);
        mScaleFactor = getMaxZoom() / 3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (!mGestureListener.mCallback.isAllowZoom())
                return super.onTouchEvent(event);

            mScaleDetector.onTouchEvent(event);
            if (!mScaleDetector.isInProgress()) mGestureDetector.onTouchEvent(event);
            int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (getScale() < 1f) {
                        zoomTo(1f, 50);
                        mGestureListener.mCallback.onScaleZoom(false);
                    }
                    break;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    protected void onZoom(float scale) {
        super.onZoom(scale);
        if (!mScaleDetector.isInProgress()) mCurrentScaleFactor = scale;
    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if ((scale + (mScaleFactor * 2)) <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    /**
     * when in area child view (image) can touch invoke
     * then need notify for parent (View pager custom) know app in mode zoom image
     * can not invoke animation of view pager
     */
    public interface IteractorGestureListenerA {

        /**
         * when double tap to zoom image
         *
         * @param isZoomed
         */
        void onDoubleTapZoom(boolean isZoomed);

        /**
         * when scroll to zoom image
         *
         * @param isZoomed
         */
        void onScrollZoom(boolean isZoomed);

        /**
         * when fling image to zoom image
         *
         * @param isCanFlingHorizolltal
         */
        void onFling(boolean isCanFlingHorizolltal);

        /**
         * when scale image
         *
         * @param isZoomed
         */
        void onScaleZoom(boolean isZoomed);

        /**
         * when image invoke zoom need check can zoom?
         *
         * @return
         */
        boolean isAllowZoom();
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private IteractorGestureListenerA mCallback;

        public GestureListener(IteractorGestureListenerA mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            try {
                //if check view pager is in mode can not zoom
                //then exit scale
                if (!mCallback.isAllowZoom())
                    return super.onDoubleTap(e);

                //scale
                float scale = getScale();
                float targetScale = scale;
                targetScale = onDoubleTapPost(scale, getMaxZoom());
                targetScale = Math.min(getMaxZoom(), Math.max(targetScale, MIN_ZOOM));
                mCurrentScaleFactor = targetScale;
                zoomTo(targetScale, e.getX(), e.getY(), 200);
                invalidate();

                //notify is zooming, mode double tap zoom
                mCallback.onDoubleTapZoom(getScale() != 1);
            } catch (Exception ee) {
                ee.printStackTrace();
            }

            return super.onDoubleTap(e);
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            try {
                //if check view pager is in mode can not zoom
                //then exit scale
                if (!mCallback.isAllowZoom())
                    return super.onScroll(e1, e2, distanceX, distanceY);

                //scale
                if (e1 == null || e2 == null) return false;
                if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
                if (mScaleDetector.isInProgress()) return false;
                if (getScale() == 1f) return false;
                scrollBy(-distanceX, -distanceY);
                invalidate();

                //notify is zooming to parent view, mode scroll zoom
                mCallback.onScrollZoom(getScale() != 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //if check view pager is in mode can not zoom
                //then exit scale
                if (!mCallback.isAllowZoom())
                    return super.onFling(e1, e2, velocityX, velocityY);

                //scale
                if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
                if (mScaleDetector.isInProgress()) return false;

                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
                    scrollBy(diffX / 2, diffY / 2, 300);
                    invalidate();
                }

                //notify is zooming to parent view, mode fling zoom
                mCallback.onFling(getScale() != 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        /**
         * ScaleListener not need check is can zoom from view parent
         * ScaleListener only invoke when on GestureListener invoke
         *
         * @param detector
         * @return
         */
        @SuppressWarnings("unused")
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //scale
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
            if (true) {
                targetScale = Math.min(getMaxZoom(), Math.max(targetScale, MIN_ZOOM));
                zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                mCurrentScaleFactor = Math.min(getMaxZoom(), Math.max(targetScale, MIN_ZOOM));
                mDoubleTapDirection = 1;
                invalidate();

                //notify is scale zoom mode
                mGestureListener.mCallback.onScaleZoom(getScale() != 1);
                return true;
            }
            return false;
        }
    }


}
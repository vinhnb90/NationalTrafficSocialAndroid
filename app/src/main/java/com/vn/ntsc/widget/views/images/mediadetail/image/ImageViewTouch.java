package com.vn.ntsc.widget.views.images.mediadetail.image;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;

public class ImageViewTouch extends ImageViewTouchBase {
    static final float SCROLL_DELTA_THRESHOLD = 1.0f;
    /**
     * minimum time between a scale event and a valid fling event
     */
    public static final long MIN_FLING_DELTA_TIME = 150;
    private float mScaleFactor;
    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;
    protected int mTouchSlop;
    protected int mDoubleTapDirection;
    protected OnGestureListener mGestureListener;
    protected OnScaleGestureListener mScaleListener;
    protected boolean mDoubleTapEnabled = true;
    protected boolean mScaleEnabled = true;
    protected boolean mScrollEnabled = true;
    private OnImageViewTouchDoubleTapListener mDoubleTapListener;
    private OnImageViewTouchSingleTapListener mSingleTapListener;

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewTouch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {

        if (context instanceof IteractorGestureListener) {
            mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

            mGestureListener = getGestureListener((IteractorGestureListener) context);
            mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);

            mScaleListener = getScaleListener();
            mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
            mDoubleTapDirection = 1;
            setQuickScaleEnabled(false);
        } else
            throw new ClassCastException("context must to be implement ImageViewTouch.IteractorGestureListenerA.");

        super.init(context, attrs, defStyle);
    }

    @TargetApi(19)
    public void setQuickScaleEnabled(boolean value) {
        mScaleDetector.setQuickScaleEnabled(value);
    }

    @TargetApi(19)
    @SuppressWarnings("unused")
    public boolean getQuickScaleEnabled() {
        return mScaleDetector.isQuickScaleEnabled();
    }

    @SuppressWarnings("unused")
    public float getScaleFactor() {
        return mScaleFactor;
    }

    public void setDoubleTapListener(OnImageViewTouchDoubleTapListener listener) {
        mDoubleTapListener = listener;
    }

    public void setSingleTapListener(OnImageViewTouchSingleTapListener listener) {
        mSingleTapListener = listener;
    }

    public void setDoubleTapEnabled(boolean value) {
        mDoubleTapEnabled = value;
    }

    public void setScaleEnabled(boolean value) {
        mScaleEnabled = value;
    }

    public void setScrollEnabled(boolean value) {
        mScrollEnabled = value;
    }

    public boolean getDoubleTapEnabled() {
        return mDoubleTapEnabled;
    }

    protected OnGestureListener getGestureListener(IteractorGestureListener callback) {
        return new GestureListener(callback);
    }

    protected OnScaleGestureListener getScaleListener() {
        return new ScaleListener();
    }

    @Override
    protected void onLayoutChanged(final int left, final int top, final int right, final int bottom) {
        super.onLayoutChanged(left, top, right, bottom);
        Log.v(TAG, "min: " + getMinScale() + ", max: " + getMaxScale() + ", result: " + (getMaxScale() - getMinScale()) / 2f);
        mScaleFactor = ((getMaxScale() - getMinScale()) / 2f) + 0.5f;
    }

    long mPointerUpTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getBitmapChanged()) {
            return false;
        }

        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_POINTER_UP) {
            mPointerUpTime = event.getEventTime();
        }

        mScaleDetector.onTouchEvent(event);

        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
                return onUp(event);
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onZoomAnimationCompleted(float scale) {

        if (DEBUG) {
            Log.d(TAG, "onZoomAnimationCompleted. scale: " + scale + ", minZoom: " + getMinScale());
        }

        if (scale < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
    }

    protected float onDoubleTapPost(float scale, final float maxZoom, final float minScale) {
        if ((scale + mScaleFactor) <= maxZoom) {
            return scale + mScaleFactor;
        } else {
            return minScale;
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }


    /**
     * Xảy ra khi drag ImageView
     * <p>
     * (khi zoom quá size của màn hình, thì cho phép drag để xem những phần nằm ngoài màn hình)
     * <p>
     * Khi image chưa được zoom qua size của màn hình thì chưa thể Drag ImageView
     * Vì ImageView đang nằm trong ViewPager, nên nó sẽ bị ViewPager chặn mất sự kiện khi vuốt Page
     * Khi Image chưa zoom quá màn hình(ko drag được) ---> set ViewPager k gửi sự kiện đến thằng con, để nó tự nhận sự kiện vuốt Page
     * Khi Image đã zoom quá màn hình (có thể drag rồi) ---> set ViewPager gửi toàn bộ sự kiện đến thằng con, để thằng con sử lý Drag
     */
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        // Improve swipe Viewpager
        // Resolve confilct onTouch Image and onTouch ViewPager
        if (getScale() > 1.0f) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        mUserScaled = true;
        scrollBy(-distanceX, -distanceY);
        invalidate();
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!canScroll()) {
            return false;
        }

        if (DEBUG) {
            Log.i(TAG, "onFling");
        }

        if (Math.abs(velocityX) > (mMinFlingVelocity * 4) || Math.abs(velocityY) > (mMinFlingVelocity * 4)) {
            if (DEBUG) {
                Log.v(TAG, "velocity: " + velocityY);
                Log.v(TAG, "diff: " + (e2.getY() - e1.getY()));
            }

            final float scale = Math.min(Math.max(2f, getScale() / 2), 3.f);

            float scaledDistanceX = ((velocityX) / mMaxFlingVelocity) * (getWidth() * scale);
            float scaledDistanceY = ((velocityY) / mMaxFlingVelocity) * (getHeight() * scale);

            if (DEBUG) {
                Log.v(TAG, "scale: " + getScale() + ", scale_final: " + scale);
                Log.v(TAG, "scaledDistanceX: " + scaledDistanceX);
                Log.v(TAG, "scaledDistanceY: " + scaledDistanceY);
            }

            mUserScaled = true;

            double total = Math.sqrt(Math.pow(scaledDistanceX, 2) + Math.pow(scaledDistanceY, 2));

            scrollBy(scaledDistanceX, scaledDistanceY, (long) Math.min(Math.max(300, total / 5), 800));

            postInvalidate();
            return true;
        }
        return false;
    }

    public boolean onDown(MotionEvent e) {
        return !getBitmapChanged();
    }

    public boolean onUp(MotionEvent e) {
        if (getBitmapChanged()) {
            return false;
        }
        if (getScale() < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return !getBitmapChanged();
    }

    public boolean canScroll() {
        if (getScale() > 1) {
            return true;
        }
        RectF bitmapRect = getBitmapRect();
        return !mViewPort.contains(bitmapRect);
    }

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     *
     * @param direction - positive direction value means scroll from right to left,
     *                  negative value means scroll from left to right
     * @return true if there is some more place to scroll, false - otherwise.
     */
    @SuppressWarnings("unused")
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollPoint);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);

        if (null == bitmapRect) {
            return false;
        }

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
            }
        }

        double bitmapScrollRectDelta = Math.abs(bitmapRect.left - mScrollPoint.x);
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private IteractorGestureListener mCallback;

        public GestureListener(IteractorGestureListener callback) {
            mCallback = callback;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (null != mSingleTapListener) {
                mSingleTapListener.onSingleTapConfirmed();
            }

            return ImageViewTouch.this.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            try {
                if (DEBUG) {
                    Log.i(TAG, "onDoubleTap. double tap enabled? " + mDoubleTapEnabled);
                }

                //if check view pager is in mode can not zoom
                //then exit scale
                if (!mCallback.isAllowZoom())
                    return super.onDoubleTap(e);

                if (mDoubleTapEnabled) {
                    if (mScaleDetector.isQuickScaleEnabled()) {
                        return true;
                    }

                    mUserScaled = true;

                    float scale = getScale();
                    float targetScale;
                    targetScale = onDoubleTapPost(scale, getMaxScale(), getMinScale());
                    targetScale = Math.min(getMaxScale(), Math.max(targetScale, getMinScale()));
                    zoomTo(targetScale, e.getX(), e.getY(), mDefaultAnimationDuration);

                }

                if (null != mDoubleTapListener) {
                    mDoubleTapListener.onDoubleTap();
                }

                //notify is zooming, mode double tap zoom
                mCallback.onDoubleTapZoom(getScale() != 1);
            } catch (Exception ee) {
                ee.printStackTrace();
            }

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isLongClickable()) {
                if (!mScaleDetector.isInProgress()) {
                    setPressed(true);
                    performLongClick();
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            try {
                //if check view pager is in mode can not zoom
                //then exit scale
                if (!mCallback.isAllowZoom())
                    return super.onScroll(e1, e2, distanceX, distanceY);


                if (!mScrollEnabled) {
                    return false;
                }
                if (e1 == null || e2 == null) {
                    return false;
                }
                if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) {
                    return false;
                }
                if (mScaleDetector.isInProgress()) {
                    return false;
                }

                boolean onScroll = ImageViewTouch.this.onScroll(e1, e2, distanceX, distanceY);
                //notify is zooming to parent view, mode scroll zoom
                mCallback.onScrollZoom(getScale() != 1);

                return onScroll;
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


                if (!mScrollEnabled) {
                return false;
            }
            if (e1 == null || e2 == null) {
                return false;
            }
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) {
                return false;
            }
            if (mScaleDetector.isInProgress()) {
                return false;
            }

            final long delta = (SystemClock.uptimeMillis() - mPointerUpTime);

            // prevent fling happening just
            // after a quick pinch to zoom
            if (delta > MIN_FLING_DELTA_TIME) {
                return ImageViewTouch.this.onFling(e1, e2, velocityX, velocityY);
            } else {
                return false;
            }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return ImageViewTouch.this.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (DEBUG) {
                Log.i(TAG, "onImageTouchDown");
            }
            stopAllAnimations();
            return ImageViewTouch.this.onDown(e);
        }
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        protected boolean mScaled = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = getScale() * detector.getScaleFactor();

            if (mScaleEnabled) {
                if (mScaled && span != 0) {
                    mUserScaled = true;
                    targetScale = Math.min(getMaxScale(), Math.max(targetScale, getMinScale() - MIN_SCALE_DIFF));
                    zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                    mDoubleTapDirection = 1;
                    invalidate();

                    //notify is scale zoom mode
                    ((GestureListener)mGestureListener).mCallback.onScaleZoom(getScale() != 1);
                    return true;
                }

                // This is to prevent a glitch the first time
                // image is scaled.
                if (!mScaled) {
                    mScaled = true;
                }
            }
            return true;
        }

    }

    public interface OnImageViewTouchDoubleTapListener {
        void onDoubleTap();
    }

    public interface OnImageViewTouchSingleTapListener {
        void onSingleTapConfirmed();
    }

    public interface IteractorGestureListener {

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
}

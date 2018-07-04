package com.vn.ntsc.ui.mediadetail.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.vn.ntsc.R;
import com.vn.ntsc.utils.LogUtils;

public class ViewPagerMedia extends ViewPager {

    /*-----------------------------------------var-----------------------------------------*/
    private static final String TAG = ViewPagerMedia.class.getName();

    private final Context mContext;
    private IOnMediaIteractor mIOnMediaIteractor;
    private IOnMediaTouchListener mIOnMediaTouchListener;
    private IDoTaskWhenTouch mIDoTaskWhenTouch;

    //min value for lock viewpager, it's important for disable swipe viewpager when swipe up|down
    private static final int THRESHOLD_MIN_VERTICAL = 50;
    private static final int THRESHOLD_MAX_VERTICAL = 250;
    private static final float THRESHOLD_TOUCH_SLOP = 100;

    //view parent of viewpager
    //when swipe anim, will opacity all view group from view pager to mViewContainer
    private View mViewContainer;

    //origin x y for restore view
    private float xOrigin, yOrigin;

    //min opacity dim view
    public static final float MIN_OPACITY = 0.4f;

    //half device height
    private float halfWindowView;
    private float xBegin, yBegin;

    //state detect mode swipe
    private boolean mIsInStateSwipeVertical;
    private boolean mIsInStateSwipeHorizontall;
    private boolean mIsInStateViewChildTaskOnTouch; //update from view child callback

    //flag allow viewChild do task
    private boolean isCanAllowDoTaskIfself;

    //flag detect viewChild is do task itself
    private boolean mViewChildIsDoTaskIfSelt;

    //distance move x and y
    private float distanceY;
    private float distanceX;

    //save systemUiVisibility default
    private int mSystemUiVisibility;

    /*-----------------------------------------instance-----------------------------------------*/
    public ViewPagerMedia(@NonNull Context context) {
        super(context);
        mContext = context;
        initData();
    }

    public ViewPagerMedia(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initData();
    }

    /*-----------------------------------------lifecycle-----------------------------------------*/

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        //get origin x y to restore view exactly
        if (xOrigin == 0 && yOrigin == 0) {
            int[] location = new int[2];
            getLocationOnScreen(location);
            xOrigin = location[0];
            yOrigin = location[1];

            //minus height status bar
            yOrigin = yOrigin - Math.abs(getHeightStatusBar());
            LogUtils.d(TAG, "onWindowFocusChanged: xOrigin" + xOrigin + " yOrigin " + yOrigin);
        }

    }

    /**
     * intercept touch event
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            //get number finger
            int numberFinger = ev.getPointerCount();

            if (mViewChildIsDoTaskIfSelt)
                return false;

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //save data ev x y
                    mIOnMediaTouchListener.onInterceptTouchDown(ev.getX(), ev.getY(), ev.getRawX(), ev.getRawY(), numberFinger);

                    //else allow viewpager invoke onTouch default
                    return super.onInterceptTouchEvent(ev);

                case MotionEvent.ACTION_MOVE:

                    //when child view not process motion event
                    //return process motion event parent view
                    //detect type move finger
                    mIOnMediaTouchListener.onInterceptTouchMove(ev.getRawX(), ev.getRawY(), numberFinger);

                    //check action after move
                    boolean isSwipeVertical = mIOnMediaIteractor.isInStateSwipeVertical();
                    boolean isSwipeHorizontall = mIOnMediaIteractor.isInStateSwipeHorizontall();

                    if (isSwipeHorizontall && !isSwipeVertical) {
                        if (!mViewChildIsDoTaskIfSelt) {
                            LogUtils.d(TAG, "onInterceptTouchEvent: 1 " + super.onInterceptTouchEvent(ev));
                            return super.onInterceptTouchEvent(ev);
                        } else {
                            LogUtils.d(TAG, "onInterceptTouchEvent: 2 false");
                            return false;
                        }
                    }

                    if (!isSwipeHorizontall && isSwipeVertical) {
                        LogUtils.d(TAG, "onInterceptTouchEvent: 3 " + super.onInterceptTouchEvent(ev));
                        return true;
                    }

                    //if isSwipeHorizontall
                    if (!isSwipeVertical && isSwipeHorizontall && !mViewChildIsDoTaskIfSelt) {
                        LogUtils.d(TAG, "onInterceptTouchEvent: 4 " + super.onInterceptTouchEvent(ev));
                        return super.onInterceptTouchEvent(ev);
                    }

                    //if not detech
                    if (!isSwipeVertical && !isSwipeHorizontall && !mViewChildIsDoTaskIfSelt) {
                        LogUtils.d(TAG, "onInterceptTouchEvent: 5 " + super.onInterceptTouchEvent(ev));
                        return super.onInterceptTouchEvent(ev);
                    }

                    //else allow viewpager invoke onTouch default
                    break;

                case MotionEvent.ACTION_UP:
                    //detect up
                    mIOnMediaTouchListener.onInterceptTouchUp();

                    //don't allow view pager invoke onTouch
                    return super.onInterceptTouchEvent(ev);
            }

            return super.onInterceptTouchEvent(ev);

        } catch (IllegalArgumentException ex) {
            //when catch ex pointerIndex out of range when call super.onInterceptTouchEvent(ev);
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * onInterceptTouchEvent (view pager) --> onTouchEvent (pager) -> onTouchEvent (viewpager)
     * if pager not a view full screen then onInterceptTouchEvent (view pager) will
     * not detect MotionEvent.ACTION_MOVE or case MotionEvent.ACTION_UP
     * will detect onTouchEvent (viewpager)
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //onTouch event will invoke
        //with condition after children view not invoked any action onTouchEvent itself
        //when any area of child view is onTouch default then we detect swipe action
        try {
            //get number finger
            int numberFinger = ev.getPointerCount();

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //save data ev x y
                    mIOnMediaTouchListener.onInterceptTouchDown(ev.getX(), ev.getY(), ev.getRawX(), ev.getRawY(), numberFinger);

                    //else allow viewpager invoke onTouch default
                    return super.onTouchEvent(ev);

                case MotionEvent.ACTION_MOVE:
                    //when child view not process motion event
                    //return process motion event parent view
                    //detect type move finger
                    LogUtils.d(TAG, "onTouchEvent ----> ACTION_MOVE ---> mViewChildIsDoTaskIfSelt = " + mViewChildIsDoTaskIfSelt);
                    mIOnMediaTouchListener.onInterceptTouchMove(ev.getRawX(), ev.getRawY(), numberFinger);

                    //check action after move
                    boolean isSwipeVertical = mIOnMediaIteractor.isInStateSwipeVertical();
                    boolean isSwipeHorizontall = mIOnMediaIteractor.isInStateSwipeHorizontall();

                    if (isSwipeHorizontall && !isSwipeVertical) {
                        LogUtils.d(TAG, "onTouchEvent: 1");
                        return super.onTouchEvent(ev);
                    }

                    if (!isSwipeHorizontall && isSwipeVertical) {
                        LogUtils.d(TAG, "onTouchEvent: 2");
                        return false;
                    }

                    LogUtils.d(TAG, "onTouchEvent: 3");
                    //else allow viewpager invoke onTouch default
                    break;

                case MotionEvent.ACTION_UP:
                    //check action after up
                    //detect up
                    mIOnMediaTouchListener.onInterceptTouchUp();

                    //don't allow view pager invoke onTouch
                    return super.onTouchEvent(ev);
            }

            return super.onTouchEvent(ev);

        } catch (IllegalArgumentException ex) {
            //when catch ex pointerIndex out of range when call super.onTouchEvent(ev);
            ex.printStackTrace();
        }

        return false;
    }

    /*-----------------------------------------override-----------------------------------------*/
    /*-----------------------------------------func-----------------------------------------*/
    private void initData() {

        //init default if not setup
        mIOnMediaIteractor = new IOnMediaIteractor() {

            @Override
            public boolean isInStateSwipeVertical() {
                return mIsInStateSwipeVertical;
            }

            @Override
            public boolean isInStateSwipeHorizontall() {
                return mIsInStateSwipeHorizontall;
            }

            @Override
            public int getScreenHeightView() {
                LogUtils.d(TAG, "ViewPagerVinhNBMedia.this.getMeasuredHeight() = " + ViewPagerMedia.this.getMeasuredHeight());
                return ViewPagerMedia.this.getMeasuredHeight();
            }

            @Override
            public void fadeAndDragView(float top, float left, float percentOpacityContainer) {
                LogUtils.d(TAG, "fadeAndDragView: percentOpacityContainer = " + percentOpacityContainer);

                // fade all view from child view -> container view
                if (mViewContainer == null) {
                    //Decodeview --> R.id.content --> R.id.toolbar -> ViewGroup -> ViewPager
                    mViewContainer = ((Activity) mContext).getWindow().getDecorView();
                }

                fadeAllChildrenView(mViewContainer, percentOpacityContainer);

                // move content
                ViewPagerMedia.this.animate()
                        .x(left)
                        .y(top)
                        .setDuration(0)
                        .start();
            }

            @Override
            public void loadAnimationFinishActivity() {
                ((Activity) mContext).overridePendingTransition(0, R.anim.fadeout);
            }

            @Override
            public void finish() {
                ((Activity) mContext).finish();
            }
        };


        mIOnMediaTouchListener = new IOnMediaTouchListener() {
            @Override
            public void onInterceptTouchDown(float x, float y, float rawX, float rawY, int numberFinger) {
                //reset flag
                isCanAllowDoTaskIfself = true;
                mIsInStateSwipeHorizontall = false;
                mIsInStateSwipeVertical = false;

                //save pivot x y when press
                xBegin = rawX;
                yBegin = rawY;

                //get half device height to min opacity when fade view
                halfWindowView = mIOnMediaIteractor.getScreenHeightView() / 2;

                if (mIDoTaskWhenTouch != null)
                    mIDoTaskWhenTouch.doTaskWhenTouchDown();
            }

            @Override
            public void onInterceptTouchMove(float rawX, float rawY, int numberFinger) {
                //invoke task do before return
                if (mIDoTaskWhenTouch != null)
                    mIDoTaskWhenTouch.doTaskWhenTouchMove(mIsInStateSwipeHorizontall, mIsInStateSwipeVertical, mViewChildIsDoTaskIfSelt);

                //if in action swipe normal then return
                if (mIsInStateSwipeHorizontall && !mIsInStateSwipeVertical) {
                    LogUtils.d(TAG, "-----------onInterceptTouchMove: 1");
                    return;
                }

                if (mViewChildIsDoTaskIfSelt) {
                    if (mIDoTaskWhenTouch != null)
                        mIDoTaskWhenTouch.doTaskWhenTouchMove(mIsInStateSwipeHorizontall, mIsInStateSwipeVertical, mViewChildIsDoTaskIfSelt);
                    return;
                }


                //calculate distance Y move
                distanceY = Math.abs(rawY - yBegin);

                if (distanceY < THRESHOLD_MIN_VERTICAL) {
                    //can zoom
                    isCanAllowDoTaskIfself = true;

                    //can swipe vertical
                    //calculate distance X move
                    distanceX = Math.abs(rawX - xBegin);
                    if (distanceX > THRESHOLD_TOUCH_SLOP) {
                        //detect swipe horizontal
                        mIsInStateSwipeHorizontall = true;
                        mIsInStateSwipeVertical = false;
                    } else {
                        //can not detect what action
                        mIsInStateSwipeHorizontall = false;
                        mIsInStateSwipeVertical = false;
                    }

                    //invoke task do before return and state is updated
                    if (mIDoTaskWhenTouch != null)
                        mIDoTaskWhenTouch.doTaskWhenTouchMove(mIsInStateSwipeHorizontall, mIsInStateSwipeVertical, mViewChildIsDoTaskIfSelt);

                    return;
                } else {
                    //example: can not allow zoom when intercept touch parent view
                    isCanAllowDoTaskIfself = false;

                    //detect swipeTopToBottom
                    mIsInStateSwipeVertical = true;
                    mIsInStateSwipeHorizontall = false;

                    //cal opacity
                    float opacity = calculateViewOpacityIntercept(distanceY);

                    //set min opacity
                    opacity = (opacity < MIN_OPACITY) ? MIN_OPACITY : opacity;

                    //fade and drag view
                    //only drag view by Y
                    mIOnMediaIteractor.fadeAndDragView(Math.abs(distanceY - THRESHOLD_MIN_VERTICAL) + yOrigin, xOrigin, opacity);

                    //invoke task do before return and state is updated
                    if (mIDoTaskWhenTouch != null)
                        mIDoTaskWhenTouch.doTaskWhenTouchMove(mIsInStateSwipeHorizontall, mIsInStateSwipeVertical, mViewChildIsDoTaskIfSelt);
                }
            }

            @Override
            public void onInterceptTouchUp() {
                //reset flag mode
                mIsInStateSwipeVertical = false;
                mIsInStateSwipeHorizontall = false;

                if (distanceY > THRESHOLD_MAX_VERTICAL) {
                    isCanAllowDoTaskIfself = false;

                    //do task when not isRestoreView
                    if (mIDoTaskWhenTouch != null)
                        mIDoTaskWhenTouch.doTaskWhenTouchUp(false);

                    //finish view
                    mIOnMediaIteractor.finish();

                    //load animation finish
                    mIOnMediaIteractor.loadAnimationFinishActivity();
                } else {
                    isCanAllowDoTaskIfself = true;

                    //do task when isRestoreView
                    if (mIDoTaskWhenTouch != null)
                        mIDoTaskWhenTouch.doTaskWhenTouchUp(true);

                    //restore view to origin pivot
                    mIOnMediaIteractor.fadeAndDragView(yOrigin, xOrigin, 1.0f);
                }

            }
        };
    }

    private int getHeightStatusBar() {
        Rect rectangle = new Rect();
        Window window = ((Activity) mContext).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;

        LogUtils.d(TAG, "onWindowFocusChanged: getHeightStatusBar" + titleBarHeight);
        return titleBarHeight;
    }

    /**
     * @param deltaY : distance Y moved
     * @return opacity of container depend on screen height
     */
    private float calculateViewOpacityIntercept(float deltaY) {
        if (halfWindowView == 0)
            return 1f;
        return 1 - (Math.abs(deltaY) / halfWindowView);
    }

    /*-----------getter setter-----------*/

    public IDoTaskWhenTouch getIDoTaskWhenTouch() {
        return mIDoTaskWhenTouch;
    }

    public void setIDoTaskWhenTouch(IDoTaskWhenTouch iDoTaskWhenTouch) {
        mIDoTaskWhenTouch = iDoTaskWhenTouch;
    }

    public IOnMediaIteractor getIOnMediaIteractor() {
        return mIOnMediaIteractor;
    }

    public void setIOnMediaIteractor(IOnMediaIteractor IOnMediaIteractor) {
        mIOnMediaIteractor = IOnMediaIteractor;
    }

    public IOnMediaTouchListener getIOnMediaTouchListener() {
        return mIOnMediaTouchListener;
    }

    public void setIOnMediaTouchListener(IOnMediaTouchListener IOnMediaTouchListener) {
        mIOnMediaTouchListener = IOnMediaTouchListener;
    }

    public View getViewContainer() {
        return mViewContainer;
    }

    /**
     * view container include view pager
     * when horizontal scroll --> view pager to fade view
     * setup view container will fade all view in container (include viewpager)
     *
     * @param viewContainer
     */
    public void setViewContainer(@Nullable View viewContainer) {
        mViewContainer = viewContainer;
    }

    /**
     * detect viewChild is do task itself
     * examp : update zoom state from image child
     * action double tap, scroll, scale, fling
     *
     * @param isDoTask
     */
    public void updateStatusViewChildIsDoTaskIfself(boolean isDoTask) {
        LogUtils.d(TAG, "updateStatusViewChildIsDoTaskIfself = " + isDoTask);
        mViewChildIsDoTaskIfSelt = isDoTask;
    }

    /**
     * child view(image view) when zoom need check can zoom?
     *
     * @return
     */
    public boolean isCanAllowDoTaskIfself() {
        return isCanAllowDoTaskIfself;
    }

    /*-----------------------------------------interface-----------------------------------------*/

    public interface IOnMediaTouchListener {

        /**
         * save origin X Y relative absolute
         * get constance 1/2 height screen to fade view (min opacity fade view =0,2 when delta Y = 1/2 height screen
         *
         * @param x             :    distance left X view
         * @param y             :    distance top Y view
         * @param rawX          : distance left X finger with screen
         * @param rawY          : distance top Y finger with screen
         * @param numberFinger: detech when is moving has multi touch zoom
         */
        void onInterceptTouchDown(float x, float y, float rawX, float rawY, int numberFinger);

        /**
         * detect move type of user
         *
         * @param rawX
         * @param rawY
         * @param numberFinger: detech when is moving has multi touch zoom
         */
        void onInterceptTouchMove(float rawX, float rawY, int numberFinger);

        /**
         * detech move up
         * finish if drag view top to bottom > 1/2 screen height
         * else restore view to origin pivot
         */
        void onInterceptTouchUp();

    }

    /**
     * when touch down, move, up viewpager then do tasks below
     * note: if setiOnMediaTouchListener then setiDoTaskWhenTouch not need
     */
    public interface IDoTaskWhenTouch {
        void doTaskWhenTouchDown();

        /**
         * process in 3 case detected touch
         *
         * @param isInStateSwipeHorizontall
         * @param isInStateSwipeVertical
         * @param isViewChildIsDoTaskIfSelf
         */
        void doTaskWhenTouchMove(boolean isInStateSwipeHorizontall, boolean isInStateSwipeVertical, boolean isViewChildIsDoTaskIfSelf);

        /**
         * two case in touch up
         *
         * @param isRestoreView = true move view to origin pivot
         */
        void doTaskWhenTouchUp(boolean isRestoreView);
    }

    public interface IOnMediaIteractor {

        /**
         * check view pager in state swipe vertical
         *
         * @return
         */
        boolean isInStateSwipeVertical();

        /**
         * check view pager in state swipe Horizontal
         *
         * @return
         */
        boolean isInStateSwipeHorizontall();


        /**
         * @return screen height of device
         */
        int getScreenHeightView();

        /**
         * move view when drag
         *
         * @param top
         * @param left
         * @param percentOpacityContainer :percent opacity to fade view
         */
        public void fadeAndDragView(float top, float left, float percentOpacityContainer);

        /**
         * load anim when finish activity
         */
        void loadAnimationFinishActivity();

        /**
         * close activity
         */
        void finish();
    }

    /**
     * fade all view from parent view -> child view
     *
     * @param view
     * @param percentOpacityContainer
     */
    private void fadeAllChildrenView(View view, float percentOpacityContainer) {
        view.animate()
                .alpha(percentOpacityContainer)
                .setDuration(0)
                .start();

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                fadeAllChildrenView(group.getChildAt(idx), percentOpacityContainer);
            }
        }
    }
}

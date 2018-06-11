package com.vn.ntsc.utils.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.vn.ntsc.utils.LogUtils;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Some method about keyboard common and
 * Based on the following Stackoverflow answer:
 * http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
 *
 * @author Created by robert on 7/28/17.
 */
@SuppressWarnings("WeakerAccess")
public class KeyboardUtils implements ViewTreeObserver.OnGlobalLayoutListener {
    private static String TAG = "KeyboardUtils";

    private float mScreenDensity = 1;
    private final static int MAGIC_NUMBER = 200;
    private boolean isSoftKeyboardOpened;

    private View mRootView;
    private Boolean prevValue = null;
    private KeyboardToggleListener mCallback;

    private static HashMap<KeyboardToggleListener, KeyboardUtils> sListenerMap = new HashMap<>();

    @Override
    public void onGlobalLayout() {
        //r will be populated with the coordinates of your view that area still visible.
        Rect r = new Rect();
        mRootView.getWindowVisibleDisplayFrame(r);

        int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
        float dp = heightDiff / mScreenDensity;
        boolean isVisible = dp > MAGIC_NUMBER;

        if (!isSoftKeyboardOpened && heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...

            isSoftKeyboardOpened = true;

            Log.e(TAG, "--->notifyOnSoftKeyboardOpened.heightDiff=" + heightDiff);

        } else if (isSoftKeyboardOpened && heightDiff < 100) {
            isSoftKeyboardOpened = false;

            Log.e(TAG, "--->notifyOnSoftKeyboardClosed");
        }
        if (mCallback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible;
            mCallback.onToggleSoftKeyboard(isVisible);
        }
    }

    /**
     * Add a new keyboard listener
     *
     * @param act      calling activity
     * @param listener callback
     */
    public static void addKeyboardToggleListener(Activity act, KeyboardToggleListener listener) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            LogUtils.e(TAG, "Software Keyboard was shown");
        } else {
            LogUtils.e(TAG, "Software Keyboard was not shown");
        }
        removeKeyboardToggleListener(listener);

        sListenerMap.put(listener, new KeyboardUtils(act, listener));
    }

    /**
     * Remove a registered listener
     *
     * @param listener {@link KeyboardToggleListener}
     */
    public static void removeKeyboardToggleListener(KeyboardToggleListener listener) {
        if (sListenerMap.containsKey(listener)) {
            KeyboardUtils k = sListenerMap.get(listener);
            k.removeListener();

            sListenerMap.remove(listener);
        }
    }

    /**
     * Remove all registered keyboard listeners
     */
    public static void removeAllKeyboardToggleListeners() {
        for (KeyboardToggleListener l : sListenerMap.keySet())
            sListenerMap.get(l).removeListener();

        sListenerMap.clear();
    }

    /**
     * Manually toggle soft keyboard visibility
     *
     * @param context calling context
     */
    public static void toggleKeyboardVisibility(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Force closes the soft keyboard
     *
     * @param activeView the view with the keyboard focus
     */
    public static void forceCloseKeyboard(View activeView) {
        InputMethodManager inputMethodManager = (InputMethodManager) activeView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activeView.getWindowToken(), 0);
    }

    private void removeListener() {
        mCallback = null;

        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private KeyboardUtils(Activity act, KeyboardToggleListener listener) {
        mCallback = listener;

        mRootView = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mScreenDensity = act.getResources().getDisplayMetrics().density;
    }

    /**
     * Hidden soft keyboard of current view focused
     *
     * @param mContext
     */
    public static void hideSoftKeyboard(Context mContext) {
        Log.e(TAG, "--->hideSoftKeyboard is calling...");
        if (mContext == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = ((Activity) mContext).getCurrentFocus();
        if (view == null) {
            return;
        }
        IBinder iBinder = view.getWindowToken();
        if (iBinder != null)
            inputMethodManager.hideSoftInputFromWindow(iBinder, 0);
    }

    /**
     * Hidden the soft keyboard of view defined when focus
     *
     * @param mContext
     * @param view
     */
    public static void hideSoftKeyboard(Context mContext, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Shows the soft keyboard of view defined
     */
    public static void showSoftKeyboard(Context mContext, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * Hidden soft keyboard when click to outside current view[editext]
     *
     * @param activity
     * @param view     root of activity
     */
    public static void hideKeyboard(final Activity activity, View view) {
        final Context mContext = activity;
        if (activity == null) {
            return;
        }

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText) && !(view instanceof Button)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(mContext);
                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            int size = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < size; i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboard(activity, innerView);
            }
        }
    }

    /**
     * Show keyboard after a delay
     *
     * @param view
     * @param timeDelay
     * @return
     */
    public static Handler showDelayKeyboard(final View view, long timeDelay) {
        Handler handler = new Handler();
        if (view == null || view.getContext() == null) {
            return handler;
        }

        if (timeDelay < 0) {
            timeDelay = 0;
        }
        view.requestFocus();
        Runnable delayRunnable = new Runnable() {

            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        };
        handler.postDelayed(delayRunnable, timeDelay);
        return handler;
    }

    /**
     * Disable the soft keyboard forever
     *
     * @param view
     */
    public static void disableSoftKeyboardForever(EditText view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * Sets whether the soft input method will be made visible when this TextView gets focused. The default is true.
             */
            view.setShowSoftInputOnFocus(false);
            return;
        }
        try {
            final Method method = EditText.class.getMethod("setShowSoftInputOnFocus", new Class[]{boolean.class});
            /**
             * Set the flag for this object to
             * the indicated boolean value.  A value of {@code true} indicates that
             * the reflected object should suppress Java language access
             * checking when it is used.  A value of {@code false} indicates
             * that the reflected object should enforce Java language access checks.
             */
            method.setAccessible(true);
            method.invoke(view, false);

        } catch (Exception e) {// ignore
            e.printStackTrace();

        }

    }


    public static boolean keybroadIsShowing(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            return true;
        } else {
            return false;
        }
    }
}



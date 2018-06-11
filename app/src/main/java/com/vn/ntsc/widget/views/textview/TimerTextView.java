package com.vn.ntsc.widget.views.textview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.Locale;

/**
 * implement timer task text view for display audio
 */
public class TimerTextView extends AppCompatTextView implements Runnable {

    private Handler handler;
    private int count = 0;

    public TimerTextView(Context context) {
        super(context);
        init();
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        handler = new Handler();
    }

    /**
     * start timer
     *
     * @param from begin from
     */
    public void startCount(int from) {
        count = from;
        handler.post(this);
    }

    /**
     * set text and format
     * @param second to set value
     */
    public void setFormatText(int second){
        setText(convertSecondsMmSs(second));
    }

    /**
     * stop timer
     */
    public void stopCount() {
        handler.removeCallbacks(this);
    }

    /**
     * stop timer & set count = 0
     */
    public void resetCount() {
        count = 0;
        stopCount();
    }

    @Override
    public void run() {
        // update ui
        setFormatText(count);

        count++;
        handler.postDelayed(this, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(this);
    }

    /**
     * @param seconds to format
     * @return mm:ss text format
     */
    public static String convertSecondsMmSs(long seconds) {
        long s = seconds % 60;
        long m = seconds / 60;

        // %02d:%02d:%02d => hh:mm:ss
        return String.format(Locale.US, "%02d:%02d", m, s);
    }
}

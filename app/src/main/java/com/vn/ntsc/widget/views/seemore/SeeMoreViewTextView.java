package com.vn.ntsc.widget.views.seemore;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import com.vn.ntsc.R;
import com.vn.ntsc.utils.LogUtils;

/**
 * Created by nankai on 2/8/2018.
 */

public class SeeMoreViewTextView extends ClickSeeMoreTextView {

    private int mDefaultLineCount = 2;
    private String mMoreHint;
    private boolean isExpand = false;
    private static final int HINT_COLOR = 0xffff4081;
    private int mHintColor = HINT_COLOR;
    public CharSequence mOriginalText;

    public SeeMoreViewTextView(Context context) {
        this(context, null);
    }

    public SeeMoreViewTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SeeMoreViewTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeeMoreViewTextView);
        mDefaultLineCount = a.getInt(R.styleable.SeeMoreViewTextView_defaultLineCount, mDefaultLineCount);
        mMoreHint = a.getString(R.styleable.SeeMoreViewTextView_defaultMoreHint);
        mHintColor = a.getColor(R.styleable.SeeMoreViewTextView_moreHintColor, HINT_COLOR);
        isExpand = a.getBoolean(R.styleable.SeeMoreViewTextView_isExpand, false);
        if (TextUtils.isEmpty(mMoreHint)) {
            mMoreHint = context.getResources().getString(R.string.see_more);
        }
        a.recycle();
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setOriginalText(CharSequence text) {
        mOriginalText = text;
        updateText();
    }

    public void setExpanded(boolean isExpand) {
        if (this.isExpand != isExpand) {
            this.isExpand = isExpand;
        }
        updateText();
        if (mListener != null) {
            mListener.onExpandChange(isExpand);
        }
    }

    public void setMoreHint(String moreHint) {
        this.mMoreHint = moreHint;
        updateText();
    }

    public boolean isExpanded() {
        return isExpand;
    }


    protected void updateText() {
        if (!isExpand) {
            setMaxLines(mDefaultLineCount);
        } else {
            setMaxLines(Integer.MAX_VALUE);
        }

        if (TextUtils.isEmpty(mOriginalText)) {
            setText(null);
            return;
        }

        setText(mOriginalText);
    }

    private boolean calculateLineCount() {
        LogUtils.i("seeMore",mOriginalText.toString());

        if (TextUtils.isEmpty(mOriginalText)) {
            return true;
        }
        Layout layout = getLayout();
        if (layout == null || isExpand) {
            setMaxLines(Integer.MAX_VALUE);
            return false;
        }
        int lineCount = layout.getLineCount();
        if (lineCount > mDefaultLineCount) {
            String substring;
            int moreLength = 0;
            String mText;

            mText = mOriginalText.toString();

            moreLength = getMoreLength(layout, moreLength, mText);
            substring = mText.substring(0, layout.getLineEnd(mDefaultLineCount - 1) - moreLength);
            SpannableStringBuilder mOriginalBuilder = new SpannableStringBuilder(String.format("%s...%s", substring, mMoreHint));
            mOriginalBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    setExpanded(true);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(mHintColor);
                    ds.bgColor = Color.TRANSPARENT;

                }
            }, substring.length() + 3, mOriginalBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            this.setText(mOriginalBuilder);
            setMaxLines(mDefaultLineCount);
        }
        return true;
    }

    private int getMoreLength(Layout layout, int moreLength, String mText) {
        int lastLine = mDefaultLineCount - 1;
        moreLength++;
        String newText = mText.substring(layout.getLineStart(lastLine), layout.getLineEnd(lastLine) - moreLength);

        while (getPaint().measureText(newText + "..." + mMoreHint) > layout.getWidth()) {
            moreLength++;
            newText = mText.substring(layout.getLineStart(lastLine), layout.getLineEnd(lastLine) - moreLength);
        }
        return moreLength;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        setExpanded(((SavedState) state).isExband);
        super.onRestoreInstanceState(((SavedState) state).getSuperState());

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        return new SavedState(parcelable, isExpand);
    }

    private static class SavedState extends BaseSavedState {
        boolean isExband;

        SavedState(Parcelable superState, boolean isExband) {
            super(superState);
            this.isExband = isExband;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isExband ? 1 : 0);
        }

        private SavedState(Parcel in) {
            super(in);
            this.isExband = in.readInt() != 0;
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

    }

    @Nullable
    private ExpandChangeListener mListener;

    public void setOnExpandChangeListener(ExpandChangeListener listener) {
        mListener = listener;
    }

    public interface ExpandChangeListener {
        void onExpandChange(boolean expanded);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int lineCount = getLayout().getLineCount();
        if (calculateLineCount()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}

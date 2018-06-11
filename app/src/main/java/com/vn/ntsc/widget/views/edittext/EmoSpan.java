package com.vn.ntsc.widget.views.edittext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

/**
 * Created by dev22 on 2/2/18.
 * span drawable for emoji
 */
public class EmoSpan extends DynamicDrawableSpan {
    private final int start;
    private final float size;
    private Drawable drawable;

    EmoSpan(int start, float size, Drawable drawable) {
        this.start = start;
        this.size = size;
        this.drawable = drawable;
    }

    @Override
    public Drawable getDrawable() {
        drawable.setBounds(0, 0, (int) size, (int) size);
        return drawable;
    }

    @Override
    public int getSize(final Paint paint, final CharSequence text, final int start,
                       final int end, final Paint.FontMetricsInt fontMetrics) {
        if (fontMetrics != null) {
            final Paint.FontMetrics paintFontMetrics = paint.getFontMetrics();
            final float fontHeight = paintFontMetrics.descent - paintFontMetrics.ascent;
            final float centerY = paintFontMetrics.ascent + fontHeight / 2;

            fontMetrics.ascent = (int) (centerY - size / 2);
            fontMetrics.top = fontMetrics.ascent;
            fontMetrics.bottom = (int) (centerY + size / 2);
            fontMetrics.descent = fontMetrics.bottom;
        }

        return (int) size;
    }

    /**
     * @return start index of emoji
     */
    public int getStart() {
        return start;
    }

    @Override
    public void draw(final Canvas canvas, final CharSequence text, final int start,
                     final int end, final float x, final int top, final int y,
                     final int bottom, final Paint paint) {
        final Drawable drawable = getDrawable();
        final Paint.FontMetrics paintFontMetrics = paint.getFontMetrics();
        final float fontHeight = paintFontMetrics.descent - paintFontMetrics.ascent;
        final float centerY = y + paintFontMetrics.descent - fontHeight / 2;
        final float transitionY = centerY - size / 2;

        canvas.save();
        canvas.translate(x, transitionY);
        drawable.draw(canvas);
        canvas.restore();
    }
}

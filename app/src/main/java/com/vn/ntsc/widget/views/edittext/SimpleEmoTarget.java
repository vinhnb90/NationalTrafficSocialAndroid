package com.vn.ntsc.widget.views.edittext;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Created by dev22 on 2/2/18.
 * glide simple target for emoji
 */
public class SimpleEmoTarget extends SimpleTarget<Drawable> {
    private final Spannable text1;
    private final float defaultEmojiSize;
    private final int start;
    private final int end;

    public SimpleEmoTarget(Spannable text1, float defaultEmojiSize, int start, int end) {
        this.text1 = text1;
        this.defaultEmojiSize = defaultEmojiSize;
        this.start = start;
        this.end = end;
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        text1.setSpan(new EmoSpan(start, defaultEmojiSize, resource), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}

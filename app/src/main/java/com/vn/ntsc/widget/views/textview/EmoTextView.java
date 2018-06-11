package com.vn.ntsc.widget.views.textview;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vn.ntsc.widget.views.autolink.AutoLinkTextview;
import com.vn.ntsc.widget.views.edittext.EmojiManager;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dev22 on 3/22/18.
 * text view to show emoji, it will convert emoji code to img tag then show emoji
 * NOTE: i must extend AutoLinkTextview instead of AppcompatTextview cause it have already implement, i cant change there all things
 *
 * @see #setText(String)
 * @see #getOriginText()
 * @see #setLimitNumberOfGif(int)
 * @see GlideImageGetter#clear(TextView)
 */
public class EmoTextView extends AutoLinkTextview {
    private float defaultEmojiSize;
    private String originText;
    /**
     * if you want show gif animation (tip: should fewer than 15)
     * animate emoji will make user have bad experiment => lag
     *
     * @see #setLimitNumberOfGif(int)
     * @see #setText(String)
     */
    private int limitNumberOfGif = 25;

    /**
     * override default number of gif to show animation
     *
     * @param limitNumberOfGif maximum number of gif
     */
    public void setLimitNumberOfGif(int limitNumberOfGif) {
        this.limitNumberOfGif = limitNumberOfGif;
    }

    public EmoTextView(Context context) {
        super(context);
        init();
    }

    public EmoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // default emoji size
        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;
    }

    /**
     * to display emoji, to get origin text to send to server {@link #getOriginText()}
     *
     * @param text contain emoji(optional) to display
     */
    public void setText(String text) {
        // store origin text before transform
        originText = text;

        Matcher matcher = Pattern.compile(EmojiManager.getInstance().getEmojiPattens()).matcher(text);
        StringBuffer sb = new StringBuffer();
        // count emoji to decide load via glide or not
        int emotionCount = 0;
        while (matcher.find()) {
            emotionCount++;
            // replace emoji code with img tag (html) to show emoji
            String image = String.format(Locale.ROOT, "<img src=\"%s\" />", EmojiManager.getInstance().getEmojiMap().get(matcher.group()));
            matcher.appendReplacement(sb, image);
        }
        matcher.appendTail(sb);

        Spanned spanned;
        if (0 < emotionCount) {
            GlideImageGetter imageGetter = new GlideImageGetter(getContext(), Glide.with(this), this, emotionCount < limitNumberOfGif, (int) defaultEmojiSize);
            spanned = Html.fromHtml(replaceNewline(sb.toString()), imageGetter, null);
            setText(spanned);
        } else {
            super.setText(text);
        }
    }

    /**
     * replace all newLine char to html
     *
     * @param htmlText input
     */
    private String replaceNewline(String htmlText) {
        return htmlText.replaceAll("\n", "<br/>");
    }

    /**
     * @return origin text when call {@link #setText(String)}
     */
    public String getOriginText() {
        return originText;
    }
}

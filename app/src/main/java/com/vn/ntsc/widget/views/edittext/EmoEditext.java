package com.vn.ntsc.widget.views.edittext;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.AppCompatEditText;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dev22 on 2/1/18.
 * edit text for display emoji
 */
public class EmoEditext extends AppCompatEditText {
    // default emoji size
    final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
    final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;

    public EmoEditext(Context context) {
        super(context);
    }

    public EmoEditext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        final Spannable text1 = getText();

        // get all exist emoji
        EmoSpan[] existSpans = text1.getSpans(0, text1.length(), EmoSpan.class);
        // get last index of span
        // abc(emoji)efd => only search emoji regex unFormatText="efd" => better regex search performance
        int lastIndex = 0;
        CharSequence unFormatText = "";
        if (existSpans.length > 0) {
            EmoSpan lastEmo = existSpans[existSpans.length - 1];
            // remove last emoji
            text1.removeSpan(lastEmo);

            // http://10.64.100.201/issues/10030#note-63
            // get start from last emoji
            lastIndex = lastEmo.getStart();
            unFormatText = text1.subSequence(lastIndex, text1.length());
        }

        final Matcher matcher = Pattern.compile(EmojiManager.getInstance().getEmojiPattens()).matcher(lastIndex > 0 ? unFormatText : text1);
        while (matcher.find()) {
            String path = EmojiManager.getInstance().getEmojiMap().get(matcher.group());
            if (path == null) continue;
            // load file if exist else load remote link
            File file = new File(path);
            Uri uri = file.exists() ? Uri.fromFile(file) : Uri.parse(path);

            if (!TextUtils.isEmpty(path))
                Glide.with(getContext())
                        .load(uri)
                        // last index + start() in case found last emoji
                        // if not last index default = 0, so it's ok
                        .into(new SimpleEmoTarget(text1, defaultEmojiSize, lastIndex + matcher.start(), lastIndex + matcher.end()));
        }
    }
}

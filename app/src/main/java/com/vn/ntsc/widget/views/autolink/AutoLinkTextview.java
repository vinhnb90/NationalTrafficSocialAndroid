package com.vn.ntsc.widget.views.autolink;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ducnv on 11/20/17.
 */

//TODO: Add Exceptions
//TODO: Add functionality to remove listener
//TODO: Add CONSTANS for common used regex
public class AutoLinkTextview extends AppCompatTextView {
    /**
     * hashmap to hold clickPatterns objects
     */
    private WeakHashMap<String, ClickPattern> clickPatterns = new WeakHashMap<>();

    public AutoLinkTextview(Context context) {
        super(context);
        init();
    }


    public AutoLinkTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Initialize fields.
     */
    private void init() {
        makePatternsClickable(this);
    }

    /**
     * Make Spannables for respective Strings and add onClick methods respectively
     *
     * @param textView
     */
    private void makePatternsClickable(TextView textView) {
        Iterator it = clickPatterns.entrySet().iterator(); // take out iterator of clickPatterns


        while (it.hasNext()) {  // Iterate through all the added patterns one by one
            Map.Entry pair = (Map.Entry) it.next();
            final ClickPattern clickPattern = (ClickPattern) pair.getValue();

            /**
             * patternsFromStrung will contain all the found patterns
             */
            ArrayList<String> patternsFromString = getPatternFromString(textView.getText().toString(), clickPattern.getRegex());

            int i = 0;
            for (String s : patternsFromString) {
                MakeSpannableStrings(textView, clickPattern, patternsFromString, i);
                i++;
            }
        }
    }

    /**
     * code to make pattern clickable and add respective onClick implementation
     *
     * @param textView
     * @param clickPattern
     * @param emailsFromString
     * @param i
     */
    private void MakeSpannableStrings(final TextView textView, final ClickPattern clickPattern, final ArrayList<String> emailsFromString, final int i) {
        SpannableString ss = new SpannableString(textView.getText());
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                String s = textView.getText().toString().substring(textView.getText().toString().indexOf(emailsFromString.get(i)), textView.getText().toString().indexOf(emailsFromString.get(i)) + emailsFromString.get(i).length());
                Log.d("MakeSpannableStrings ", "11  " + textView.getText().subSequence(textView.getText().toString().indexOf(emailsFromString.get(i)), textView.getText().toString().indexOf(emailsFromString.get(i)) + emailsFromString.get(i).length()));
                clickPattern.getOnClickListener().onClickAutoLink(s);

            }
        };
        ss.setSpan(span1, textView.getText().toString().indexOf(emailsFromString.get(i)), textView.getText().toString().indexOf(emailsFromString.get(i)) + emailsFromString.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * parsing pattern
     *
     * @param text
     * @return arraylist of emails
     */
    public ArrayList<String> getPatternFromString(String text, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        ArrayList<String> patterns = new ArrayList<>();
        while (matcher.find()) {
            patterns.add(matcher.group());
        }
        return patterns;

    }

    public void addClickPattern(String tag, ClickPattern clickPattern) {
        if (clickPatterns != null) {
            clickPatterns.put(tag, clickPattern);
            makePatternsClickable(this);
        }
    }
}
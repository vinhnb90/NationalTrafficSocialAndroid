package com.vn.ntsc.widget.views.checkbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vn.ntsc.R;

/**
 * checkbox button right of text view
 */
public class MyCheckboxButton extends FrameLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private String textValue;
    private boolean isChecked;
    private CheckBox checkbox;
    private CheckedChangeListener listener;

    public MyCheckboxButton(Context context) {
        super(context);
        init();
    }

    public MyCheckboxButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MyCheckboxButton, 0, 0);
        try {
            //get the text and colors specified using the names in attrs.xml
            textValue = a.getString(R.styleable.MyCheckboxButton_text);
            isChecked = a.getBoolean(R.styleable.MyCheckboxButton_isChecked, false);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_radio_buttom_right, this, true);
        view.setOnClickListener(this);
        TextView text = view.findViewById(R.id.txt);
        text.setOnClickListener(this);
        if (textValue != null) text.setText(textValue);
        checkbox = view.findViewById(R.id.cbx);
        checkbox.setChecked(isChecked);
        checkbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        checkbox.performClick();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && listener != null) listener.onChecked(getId());
    }

    /**
     * register listener
     */
    public void setListener(CheckedChangeListener listener) {
        this.listener = listener;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        checkbox.setChecked(checked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public interface CheckedChangeListener {
        /**
         * only listen when checked = true
         */
        void onChecked(int id);
    }
}

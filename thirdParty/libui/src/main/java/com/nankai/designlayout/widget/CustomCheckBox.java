package com.nankai.designlayout.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.nankai.designlayout.R;

/**
 * Created by nankai on 12/16/2016.
 */

public class CustomCheckBox extends AppCompatImageView {

    private boolean isCheck;

    public CustomCheckBox(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isCheck = false;
    }

    public void setCanEdit(boolean z) {
        setEnabled(z);
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void setStatus(boolean z) {
        setCheck(z);
        if (z) {
            setBackgroundResource(R.drawable.update_status_checkbox_checked);
        } else {
            setBackgroundResource(R.drawable.update_status_checkbox_normal);
        }
    }
}

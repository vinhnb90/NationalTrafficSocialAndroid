package com.vn.ntsc.widget.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.login.LoginActivity;
import com.vn.ntsc.utils.Utils;

public class LayoutEmpty extends ConstraintLayout {

    public LayoutEmpty(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LayoutEmpty(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LayoutEmpty);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this, true);
        TextView emptyView = view.findViewById(R.id.empty_view);
        boolean isCheckLogin = true;
        if (typedArray.hasValue(R.styleable.LayoutEmpty_check_login))
            isCheckLogin = typedArray.getBoolean(R.styleable.LayoutEmpty_check_login, true);

        if (Utils.isEmptyOrNull(UserPreferences.getInstance().getToken()) && isCheckLogin) {
            emptyView.setText(Html.fromHtml(getResources().getString(R.string.common_link_sign_in)));
            emptyView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginActivity.launch((AppCompatActivity) context);
                }
            });
        } else {
            if (typedArray.hasValue(R.styleable.LayoutEmpty_text_empty)) {
                emptyView.setText(typedArray.getText(R.styleable.LayoutEmpty_text_empty));
            } else {
                emptyView.setText(R.string.common_empty);
            }
        }
    }
}

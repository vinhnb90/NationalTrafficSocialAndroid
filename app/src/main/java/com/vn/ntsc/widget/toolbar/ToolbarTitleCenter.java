package com.vn.ntsc.widget.toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;

/**
 * Created by nankai on 4/13/2018.
 */

public class ToolbarTitleCenter extends Toolbar {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;

    private View view;
    private TextView title;

    private RelativeLayout layoutButtonRight;
    private TextView textRight;
    private ImageView imgRight;

    private TextView textLeft;

    private ActionBar actionbar;

    boolean isDisplayTextLeft = false;

    private int type = TYPE_NONE;
    private String TAG = ToolbarTitleCenter.class.getSimpleName();

    public ToolbarTitleCenter(Context context) {
        super(context);
        initView(context, null);
    }

    public ToolbarTitleCenter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ToolbarTitleCenter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        String strTitle = "";
        String strLeft = "";

        int colorBackground = -1;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToolbarTitleCenter);

            type = ta.getInt(R.styleable.ToolbarTitleCenter_type_button_right, TYPE_NONE);
            colorBackground = ta.getResourceId(R.styleable.ToolbarTitleCenter_background, R.color.colorPrimary);

            if (type != TYPE_NONE) {
                view = LayoutInflater.from(context).inflate(R.layout.view_toolbar_button_right_template, this, true);

                textRight = view.findViewById(R.id.toolbar_text_right);
                imgRight = view.findViewById(R.id.toolbar_image_right);
                layoutButtonRight = view.findViewById(R.id.toolbar_layout_button_right);

                if (type == TYPE_IMAGE) {
                    imgRight.setVisibility(VISIBLE);
                    textRight.setVisibility(GONE);
                    int icon = ta.getResourceId(R.styleable.ToolbarTitleCenter_icon_right, R.drawable.ic_navibar_back);
                    imgRight.setImageResource(icon);
                } else {
                    imgRight.setVisibility(GONE);
                    textRight.setVisibility(VISIBLE);
                    String txtR = ta.getString(R.styleable.ToolbarTitleCenter_text_right);
                    textRight.setText(txtR);
                }

            } else {
                view = LayoutInflater.from(context).inflate(R.layout.view_toolbar_template, this, true);
            }

            strTitle = ta.getString(R.styleable.ToolbarTitleCenter_text_title);
            strLeft = ta.getString(R.styleable.ToolbarTitleCenter_text_left);

            ta.recycle();
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.view_toolbar_template, this, true);
        }

        title = view.findViewById(R.id.toolbar_title);
        textLeft = view.findViewById(R.id.toolbar_left);

        title.setText(strTitle);
        if (strLeft != null && !strLeft.equals("")) {
            isDisplayTextLeft = true;
            textLeft.setVisibility(VISIBLE);
            textLeft.setText(strLeft);
        } else {
            textLeft.setVisibility(INVISIBLE);
        }

        if (colorBackground == -1) {
            setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            setBackgroundColor(getResources().getColor(colorBackground));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        @SuppressLint("DrawAllocation") int[] location = new int[2];
        title.getLocationOnScreen(location);
        title.setTranslationX(title.getTranslationX() + (-location[0] + AppController.SCREEN_WIDTH / 2 - title.getWidth() / 2));
    }

    public ToolbarTitleCenter setActionbar(ActionBar actionbar) {
        this.actionbar = actionbar;
        actionbar.setDisplayShowTitleEnabled(false);
        return this;
    }

    public ToolbarTitleCenter setTitleCenter(@StringRes int str) {
        if (this.actionbar != null) {
            actionbar.setDisplayShowTitleEnabled(false);
        } else {
            Log.w(TAG, "actionbar is null");
        }

        title.setText(str);

        requestLayout();
        return this;
    }

    public ToolbarTitleCenter setTitleCenter(@NonNull String str) {
        if (this.actionbar != null) {
            actionbar.setDisplayShowTitleEnabled(false);
        } else {
            Log.w(TAG, "actionbar is null");
        }

        title.setText(str);

        requestLayout();
        return this;
    }

    public ToolbarTitleCenter setTextRight(@NonNull String str) {
        if (this.textRight != null) {
            textRight.setText(str);
        } else {
            Log.w(TAG, "button right is null");
        }
        return this;
    }

    public ToolbarTitleCenter setTextRight(@StringRes int str) {
        if (this.textRight != null) {
            textRight.setText(str);
        } else {
            Log.w(TAG, "button right is null");
        }
        return this;
    }

    public ToolbarTitleCenter setIconRight(@NonNull Drawable drawable) {
        if (this.imgRight != null) {
            imgRight.setImageDrawable(drawable);
        } else {
            Log.w(TAG, "button right is null");
        }
        return this;
    }

    public ToolbarTitleCenter setIconRight(@DrawableRes int drawable) {
        if (this.imgRight != null) {
            imgRight.setImageResource(drawable);
        } else {
            Log.w(TAG, "button right is null");
        }
        return this;
    }

    public ToolbarTitleCenter setDisplayHomeAsUpEnabled(boolean isEnable) {
        if (this.actionbar != null && !isDisplayTextLeft) {
            actionbar.setDisplayHomeAsUpEnabled(isEnable);
            actionbar.setDisplayShowHomeEnabled(isEnable);
            setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AppCompatActivity) getContext()).onBackPressed(); // Implemented by activity
                }
            });
        } else {
            Log.w(TAG, "actionbar is null");
        }
        return this;
    }

    public ToolbarTitleCenter setButtonRightListener(final ToolbarButtonRightClickListener rightListener) {
        if (type != TYPE_NONE) {
            if (this.layoutButtonRight != null) {
                layoutButtonRight.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rightListener.onToolbarButtonRightClick(v);
                    }
                });
            }
        }
        return this;
    }

    public ToolbarTitleCenter setTitleListener(final ToolbarTitleClickListener titleListener) {
        if (this.title != null) {
            title.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleListener.onToolbarTitleClick(v);
                }
            });
        } else {
            Log.w(TAG, "title is null");
        }
        return this;
    }

    public ToolbarTitleCenter setButtonLeftListener(final ToolbarButtonLeftClickListener buttonLeftListener) {
        if (this.textLeft != null) {
            textLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonLeftListener.onToolbarButtonLeftClick(v);
                }
            });
        } else {
            Log.w(TAG, "textLeft is null");
        }
        return this;
    }

    public ToolbarTitleCenter setVisibilityButtonRight(boolean isVisibility) {
        if (layoutButtonRight != null) {
            layoutButtonRight.setVisibility(isVisibility ? VISIBLE : INVISIBLE);
        } else {
            Log.w(TAG, "layoutButtonRight is null");
        }
        return this;
    }
}

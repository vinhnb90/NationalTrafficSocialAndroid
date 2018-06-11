package com.nankai.designlayout.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nankai.designlayout.R;
import com.nankai.designlayout.dialog.enums.Style;

/**
 * Created by nankai on 11/7/2016.
 */

public class DialogMaterial {

    protected final Builder mBuilder;

    public final Builder getBuilder() {
        return mBuilder;
    }

    private DialogMaterial(Builder builder) {
        mBuilder = builder;
        if (mBuilder.dialog != null) {
            if (mBuilder.dialog.isShowing()) {
                mBuilder.dialog.dismiss();
            }
            mBuilder.dialog = null;
        }
        mBuilder.dialog = initDialogMaterial(builder).create();
    }

    @UiThread
    public void changeButtonTextColor() {
        //Now setup to change color of the positive button
        if (mBuilder.positiveColor != -1)
            mBuilder.dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mBuilder.positiveColor);
        //Now setup to change color of the negative button
        if (mBuilder.negativeColor != -1)
            mBuilder.dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mBuilder.negativeColor);
        //Now setup to change color of the neutral button
        if (mBuilder.neutralColor != -1)
            mBuilder.dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(mBuilder.neutralColor);
    }

    @UiThread
    private void show() throws Exception{
        if (mBuilder != null && mBuilder.dialog != null)
            if (mBuilder.context != null && mBuilder.context instanceof AppCompatActivity)
                if (!((AppCompatActivity) mBuilder.context).isFinishing())
                    mBuilder.dialog.show();
    }

    @UiThread
    private AlertDialog.Builder initDialogMaterial(final Builder builder) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(builder.context, R.style.Dialog_SingleChoice);
        // Set cancelable
        dialogBuilder.setCancelable(builder.isCancelable);
        // Set style
        dialogBuilder.setView(initStyle(builder));

        // Set positive button
        if (builder.positive != null && builder.positive.length() != 0 && builder.positiveCallback != null)
            dialogBuilder.setPositiveButton(builder.positive, builder.positiveCallback);

        // set negative button
        if (builder.negative != null && builder.negative.length() != 0 && builder.negativeCallback != null)
            dialogBuilder.setNegativeButton(builder.negative, builder.negativeCallback);

        // Set neutral button
        if (builder.neutral != null && builder.neutral.length() != 0 && builder.neutralCallback != null)
            dialogBuilder.setNeutralButton(builder.neutral, builder.neutralCallback);

        return dialogBuilder;
    }

    @UiThread
    private View initStyle(final Builder builder) {
        LayoutInflater inflater = LayoutInflater.from(builder.context);
        View contentView;

        switch (builder.style) {
            case Style.HEADER_WITH_ICON:
                contentView = inflater.inflate(R.layout.style_dialog_header_icon, null);
                break;
            case Style.HEADER_WITH_TITLE:
                contentView = inflater.inflate(R.layout.style_dialog_header_title, null);
                break;
            default:
                contentView = inflater.inflate(R.layout.style_dialog_header_icon, null);
                break;
        }

        RelativeLayout dialogHeaderColor = contentView.findViewById(R.id.md_styled_header_color);
        ImageView dialogHeader = contentView.findViewById(R.id.md_styled_header);
        ImageView dialogPic = contentView.findViewById(R.id.md_styled_header_pic);
        TextView dialogTitle = contentView.findViewById(R.id.md_styled_dialog_title);

        TextView dialogDescription = contentView.findViewById(R.id.md_styled_dialog_description);
        FrameLayout dialogCustomViewGroup = contentView.findViewById(R.id.md_styled_dialog_custom_view);
        View dialogDivider = contentView.findViewById(R.id.md_styled_dialog_divider);

        //is show
        if (builder.isShowHeader || builder.style != Style.HEADER_WITH_NOT_HEADER) {
            if (builder.style == Style.HEADER_WITH_ICON) {
                dialogHeaderColor.setVisibility(View.VISIBLE);
                // Set header color or drawable
                if (builder.headerDrawable != null) {
                    dialogHeader.setImageDrawable(builder.headerDrawable);
                    if (builder.isDarkerOverlay) {
                        dialogHeader.setColorFilter(Color.rgb(123, 123, 123), PorterDuff.Mode.MULTIPLY);
                    }
                }
                dialogHeaderColor.setBackgroundColor(builder.primaryColor);
                dialogHeader.setScaleType(builder.headerScaleType);
                if (builder.iconDrawable != null) {
                    dialogPic.setImageDrawable(builder.iconDrawable);
                }
            }
            // Set dialog title
            if (builder.title != null && builder.title.length() != 0) {
                dialogTitle.setText(builder.title);
            } else {
                dialogTitle.setVisibility(View.GONE);
            }
            // Set icon animation
            if (builder.isIconAnimation) {
                if (builder.style != Style.HEADER_WITH_TITLE) {
                    UtilsAnimation.zoomInAndOutAnimation(builder.context, dialogPic);
                }
            }
        } else {
            dialogHeaderColor.setVisibility(View.GONE);
        }

        //Set the custom view
        if (builder.customView != null) {
            dialogCustomViewGroup.addView(builder.customView);
            dialogCustomViewGroup.setPadding(builder.customViewPaddingLeft, builder.customViewPaddingTop, builder.customViewPaddingRight, builder.customViewPaddingBottom);
        }

        // Set dialog description
        if (builder.description != null && builder.description.length() != 0) {
            dialogDescription.setText(builder.description);
            // Set scrollable
            dialogDescription.setVerticalScrollBarEnabled(builder.isScrollable);
            if (builder.isScrollable) {
                dialogDescription.setMaxLines(builder.maxLines);
                dialogDescription.setMovementMethod(new ScrollingMovementMethod());
            } else {
                dialogDescription.setMaxLines(Integer.MAX_VALUE);
            }
        } else {
            dialogDescription.setVisibility(View.GONE);
        }

        // Show dialog divider if enabled
        if (builder.isDialogDivider) {
            dialogDivider.setVisibility(View.VISIBLE);
        }else {
            dialogDivider.setVisibility(View.GONE);
        }

        return contentView;
    }

    public static class Builder implements IBuilder {

        protected Context context;
        private android.support.v7.app.AlertDialog dialog;
        @Style
        protected int style;
        protected View customView;
        protected Drawable headerDrawable, iconDrawable;
        protected Integer primaryColor, maxLines;
        protected CharSequence title, description;
        protected ImageView.ScaleType headerScaleType;
        protected boolean isIconAnimation, isDialogAnimation, isDialogDivider,
                isCancelable, isScrollable, isDarkerOverlay, isShowHeader;
        protected int customViewPaddingLeft, customViewPaddingTop, customViewPaddingRight, customViewPaddingBottom;
        protected int tag;//identifier for each type dialog
        protected @ColorInt
        int positiveColor = -1, negativeColor = -1, neutralColor = -1;

        protected CharSequence positive, negative, neutral;
        protected DialogInterface.OnClickListener positiveCallback, negativeCallback, neutralCallback;

        public Builder(Context context) {
            this.context = context;
            this.style = Style.HEADER_WITH_ICON;
            this.isIconAnimation = true;
            this.isDialogAnimation = false;
            this.isDialogDivider = false;
            this.isDarkerOverlay = false;
            this.isCancelable = true;
            this.primaryColor = UtilsLibrary.getPrimaryColor(context);
            this.isScrollable = false;
            this.maxLines = 5;
            this.headerScaleType = ImageView.ScaleType.CENTER_CROP;
            this.isShowHeader = true;
        }

        @Override
        public Builder setCustomView(View customView) {
            this.customView = customView;
            this.customViewPaddingLeft = 0;
            this.customViewPaddingRight = 0;
            this.customViewPaddingTop = 0;
            this.customViewPaddingBottom = 0;
            return this;
        }

        @Override
        public Builder setCustomView(View customView, int left, int top, int right, int bottom) {
            this.customView = customView;
            this.customViewPaddingLeft = UtilsLibrary.dpToPixels(context, left);
            this.customViewPaddingRight = UtilsLibrary.dpToPixels(context, right);
            this.customViewPaddingTop = UtilsLibrary.dpToPixels(context, top);
            this.customViewPaddingBottom = UtilsLibrary.dpToPixels(context, bottom);
            return this;
        }

        public Builder setTag(int tag) {
            this.tag = tag;
            return this;
        }

        public int getTag() {
            return tag;
        }

        @Override
        public Builder setStyle(@Style int style) {
            this.style = style;
            return this;
        }

        @Override
        public Builder withIconAnimation(Boolean withAnimation) {
            this.isIconAnimation = withAnimation;
            return this;
        }

        @Override
        public Builder withDialogAnimation(Boolean withAnimation) {
            this.isDialogAnimation = withAnimation;
            return this;
        }

        @Override
        public Builder withDivider(Boolean withDivider) {
            this.isDialogDivider = withDivider;
            return this;
        }

        @Override
        public Builder withDarkerOverlay(Boolean withDarkerOverlay) {
            this.isDarkerOverlay = withDarkerOverlay;
            return this;
        }

        @Override
        public Builder setIcon(@NonNull Drawable icon) {
            this.iconDrawable = icon;
            return this;
        }

        @Override
        public Builder setIcon(@DrawableRes Integer iconRes) {
            this.iconDrawable = ResourcesCompat.getDrawable(context.getResources(), iconRes, null);
            return this;
        }

        @Override
        public Builder setTitle(@StringRes int titleRes) {
            setTitle(this.context.getString(titleRes));
            return this;
        }

        @Override
        public Builder setTitle(@NonNull CharSequence title) {
            this.title = title;
            return this;
        }

        @Override
        public Builder setContent(@StringRes int descriptionRes) {
            setContent(this.context.getString(descriptionRes));
            return this;
        }

        @Override
        public Builder setContent(@NonNull CharSequence description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder setHeaderColor(@ColorRes int color) {
            this.primaryColor = UtilsLibrary.getColor(context, color);
            return this;
        }

        @Override
        public Builder setHeaderColorInt(@ColorInt int color) {
            this.primaryColor = color;
            return this;
        }

        @Override
        public Builder setHeaderDrawable(@NonNull Drawable drawable) {
            this.headerDrawable = drawable;
            return this;
        }

        @Override
        public Builder setHeaderDrawable(@DrawableRes Integer drawableRes) {
            this.headerDrawable = ResourcesCompat.getDrawable(context.getResources(), drawableRes, null);
            return this;
        }

        @Override
        public Builder removeHeader() {
            this.isShowHeader = false;
            this.style = Style.HEADER_WITH_NOT_HEADER;
            return this;
        }

        @Override
        @Deprecated
        public Builder onPositive(String text, DialogInterface.OnClickListener callback) {
            this.positive = text;
            this.positiveCallback = callback;
            return this;
        }

        @Override
        public Builder onPositive(@StringRes int text, DialogInterface.OnClickListener callback) {
            onPositive(this.context.getString(text), callback);
            return this;
        }

        @Override
        @Deprecated
        public Builder onNegative(String text, DialogInterface.OnClickListener callback) {
            this.negative = text;
            this.negativeCallback = callback;
            return this;
        }

        @Override
        public Builder onNegative(@StringRes int text, DialogInterface.OnClickListener callback) {
            onNegative(this.context.getString(text), callback);
            return this;
        }

        @Override
        @Deprecated
        public Builder onNeutral(String text, DialogInterface.OnClickListener callback) {
            this.neutral = text;
            this.neutralCallback = callback;
            return this;
        }

        @Override
        public Builder onNeutral(@StringRes int text, DialogInterface.OnClickListener callback) {
            onNeutral(this.context.getString(text), callback);
            return this;
        }

        @Override
        public Builder setHeaderScaleType(ImageView.ScaleType scaleType) {
            this.headerScaleType = scaleType;
            return this;
        }

        @Override
        public Builder setCancelable(Boolean cancelable) {
            this.isCancelable = cancelable;
            return this;
        }

        @Override
        public Builder setScrollable(Boolean scrollable) {
            this.isScrollable = scrollable;
            return this;
        }

        @Override
        public Builder setScrollable(Boolean scrollable, Integer maxLines) {
            this.isScrollable = scrollable;
            this.maxLines = maxLines;
            return this;
        }

        @Override
        public Builder setButtonTextColor(int color) {
            this.positiveColor = color;
            this.negativeColor = color;
            this.neutralColor = color;
            return this;
        }

        @Override
        public Builder setPositiveTextColor(int color) {
            this.positiveColor = color;
            return this;
        }

        @Override
        public Builder setNegativeTextColor(int color) {
            this.negativeColor = color;
            return this;
        }

        @Override
        public Builder setNeutralTextColor(int color) {
            this.neutralColor = color;
            return this;
        }

        @UiThread
        public DialogMaterial build() {
            return new DialogMaterial(this);
        }

        public boolean isShowing() {
            if (dialog == null)
                return false;
            return dialog.isShowing();
        }

        public void dismiss() {
            dialog.dismiss();
        }

        @UiThread
        public DialogMaterial show() {
            DialogMaterial dialogMaterial = build();
            try {
                dialogMaterial.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Setup to change color of the buttons if available
            dialogMaterial.changeButtonTextColor();
            return dialogMaterial;
        }
    }
}

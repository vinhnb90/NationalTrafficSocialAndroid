package com.nankai.designlayout.dialog;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;

import com.nankai.designlayout.dialog.enums.Style;

public interface IBuilder {

    /**
     * Set custom view for the dialog.
     *
     * @param customView to apply
     * @return this
     */
    DialogMaterial.Builder setCustomView(View customView);

    /**
     * Set custom view for the dialog with optional padding in DP.
     *
     * @param customView to apply
     * @param left       padding left in DP
     * @param top        padding top in DP
     * @param right      padding right in DP
     * @param bottom     padding bottom in DP
     * @return this
     */
    DialogMaterial.Builder setCustomView(View customView, int left, int top, int right, int bottom);

    /**
     * Set an style for the dialog. Default: Style.STYLE_HEADER.
     *
     * @param style to apply
     * @return this
     * @see
     */
    DialogMaterial.Builder setStyle(@Style int style);

    /**
     * Set if the header icon will be displayed with an initial animation. Default: true.
     *
     * @param withAnimation true to enable animation, false otherwise
     * @return this
     */
    DialogMaterial.Builder withIconAnimation(Boolean withAnimation);

    /**
     * Set if the dialog will be displayed with an open and close animation. Default: false.
     *
     * @param withAnimation true to enable animation, false otherwise
     * @return this
     */
    DialogMaterial.Builder withDialogAnimation(Boolean withAnimation);

    /**
     * Set if the divider will be displayed before the buttons and after the dialog content. Default: false.
     *
     * @param withDivider true to enable animation, false otherwise
     * @return this
     */
    DialogMaterial.Builder withDivider(Boolean withDivider);

    /**
     * Set if the header will display a gray/darker overlay. Default: false.
     *
     * @param withDarkerOverlay true to apply a darker overlay, false otherwise
     * @return this
     */
    DialogMaterial.Builder withDarkerOverlay(Boolean withDarkerOverlay);

    /**
     * Set an icon for the dialog header
     *
     * @param icon to show
     * @return this
     */
    DialogMaterial.Builder setIcon(@NonNull Drawable icon);

    /**
     * Set an icon for the dialog header
     *
     * @param iconRes to show
     * @return this
     */
    DialogMaterial.Builder setIcon(@DrawableRes Integer iconRes);

    /**
     * Set a title for the dialog
     *
     * @param titleRes to show
     * @return this
     */
    DialogMaterial.Builder setTitle(@StringRes int titleRes);

    /**
     * Set a title for the dialog
     *
     * @param title to show
     * @return this
     */
    DialogMaterial.Builder setTitle(@NonNull CharSequence title);

    /**
     * Set a description for the dialog
     *
     * @param descriptionRes to show
     * @return this
     */
    DialogMaterial.Builder setContent(@StringRes int descriptionRes);

    /**
     * Set a description for the dialog
     *
     * @param description to show
     * @return this
     */
    DialogMaterial.Builder setContent(@NonNull CharSequence description);

    /**
     * Set a color for the dialog header. Default: Theme primary color.
     *
     * @param color for the header
     * @return this
     */
    DialogMaterial.Builder setHeaderColor(@ColorRes int color);

    /**
     * Set a color for the dialog header. Default: Theme primary color.
     *
     * @param color for the header
     * @return this
     */
    DialogMaterial.Builder setHeaderColorInt(@ColorInt int color);

    /**
     * Set an image for the dialog header.
     *
     * @param drawable image for the header
     * @return this
     */
    DialogMaterial.Builder setHeaderDrawable(@NonNull Drawable drawable);

    /**
     * Set an image for the dialog header
     *
     * @param drawableRes image for the header
     * @return this
     */
    DialogMaterial.Builder setHeaderDrawable(@DrawableRes Integer drawableRes);

    DialogMaterial.Builder removeHeader();

    /**
     * Set a positive button for the dialog
     *
     * @param text     for the button
     * @param callback action to do
     * @return this
     */
    DialogMaterial.Builder onPositive(String text, DialogInterface.OnClickListener callback);

    /**
     * Set a positive button for the dialog
     *
     * @param text     for the button
     * @param callback action to do
     * @return this
     */
    DialogMaterial.Builder onPositive(@StringRes int text, DialogInterface.OnClickListener callback);

    /**
     * Set a negative button for the dialog
     *
     * @param text     for the button
     * @param callback action to do
     * @return this
     */
    DialogMaterial.Builder onNegative(String text, DialogInterface.OnClickListener callback);

    /**
     * Set a negative button for the dialog
     *
     * @param text     for the button
     * @param callback action to do
     * @return this
     */
    DialogMaterial.Builder onNegative(@StringRes int text, DialogInterface.OnClickListener callback);

    /**
     * Set a neutral button for the dialog
     *
     * @param text     for the button
     * @param callback action to do
     * @return this
     */
    DialogMaterial.Builder onNeutral(String text, DialogInterface.OnClickListener callback);

    /**
     * Set a neutral button for the dialog
     *
     * @param text     for the button
     * @param callback action to do
     * @return this
     */
    DialogMaterial.Builder onNeutral(@StringRes int text, DialogInterface.OnClickListener callback);

    /**
     * Set the scale type for the header ImageView.
     *
     * @param scaleType the scale type for the header ImageView
     * @return this
     */
    DialogMaterial.Builder setHeaderScaleType(ImageView.ScaleType scaleType);

    /**
     * Set if the dialog will be hidden when touching outside
     *
     * @param cancelable true to enable, false otherwise
     * @return this
     */
    DialogMaterial.Builder setCancelable(Boolean cancelable);

    /**
     * Set if the description will be isScrollable. Default: false.
     *
     * @param scrollable true to enable isScrollable description, false otherwise
     * @return this
     */
    DialogMaterial.Builder setScrollable(Boolean scrollable);

    /**
     * Set if the description will be isScrollable, with custom maximum lines. Default: false, 5.
     *
     * @param scrollable true to enable isScrollable description, false otherwise
     * @return this
     */
    DialogMaterial.Builder setScrollable(Boolean scrollable, Integer maxLines);

    /**
     * Set Text Color for all Button
     *
     * @param @ColorInt int color
     * @return this
     */
    DialogMaterial.Builder setButtonTextColor(@ColorInt int color);
    /**
     * Set Text Color for Positive Button
     *
     * @param @ColorInt int color
     * @return this
     */
    DialogMaterial.Builder setPositiveTextColor(@ColorInt int color);
    /**
     * Set Text Color for Negative Button
     *
     * @param @ColorInt int color
     * @return this
     */
    DialogMaterial.Builder setNegativeTextColor(@ColorInt int color);
    /**
     * Set Text Color for Neutral Button
     *
     * @param @ColorInt int color
     * @return this
     */
    DialogMaterial.Builder setNeutralTextColor(@ColorInt int color);

}

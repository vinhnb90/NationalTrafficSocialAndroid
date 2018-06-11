package com.vn.ntsc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.Target;
import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.ui.tagfriends.ChipListener;
import com.vn.ntsc.widget.views.images.CleanBackgroundTransform;
import com.vn.ntsc.widget.views.images.bluer.BlurTransformation;

/**
 * What will be effect of this on performance of application and memory?
 * Glide provides so many .with() methods for a reason: it follows lifecycle.
 * <p>
 * Imagine a Fragment that is dynamically added to an Activity. In its onCreateView method it starts a Glide load of a 3MB image. Now, what if the user presses the back button and the Fragment is removed or the whole activity is closed?
 * <p>
 * If you use with(getActivity().getApplicationContext()) nothing will happen, all 3MBs of data is downloaded and then decoded, cached, probably even set to the ImageView, which is then garbage collected, because the only reference to it was from Glide internals.
 * If you use with((Fragment)this) Glide subscribes to the Fragment's lifecycle events and as soon as the Fragment is stopped, the any outstanding request should be paused; and when destroyed, all pending requests be cleared. This means that the image download will stop midway and no more resources will be used by that dead Fragment.
 * If you use with(getActivity()) Glide subscribes to the Activity's lifecycle events and the same thing happens as above, but only when the Activity is stopped or destroyed.
 * So the best practice is to use the closest possible context/fragment to avoid unused request completions! (There's also a manual way to stop a load: Glide.clear(ImageView|Target).)
 * <p>
 * Created by nankai on 8/3/2017.
 */

public class ImagesUtils {

    private static final String TAG = ImagesUtils.class.getSimpleName();

    //-------------------------------------------------------------------------//
    //------------------------ constant ---------------------------------------//
    //-------------------------------------------------------------------------//

    public static float THUMBNAIL = 0.7f;

    public static final RequestOptions OPTIONS_IMG_CHAT_DEFAULT = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .encodeFormat(Bitmap.CompressFormat.JPEG)
            .priority(Priority.HIGH)
            .placeholder(R.drawable.ic_img_send_default)
            .diskCacheStrategy(DiskCacheStrategy.DATA);

    public static final RequestOptions OPTION_ROUNDED_AVATAR_WOMAN_BORDER = RequestOptions
            .circleCropTransform()
            .format(DecodeFormat.PREFER_RGB_565)
            .encodeFormat(Bitmap.CompressFormat.WEBP)
            .priority(Priority.HIGH)
            .placeholder(R.drawable.ic_avatar_circle_female_border)
            .error(R.drawable.ic_avatar_circle_female_border)
            .fallback(R.drawable.ic_avatar_circle_female_border)
            .diskCacheStrategy(DiskCacheStrategy.DATA);

    public static final RequestOptions OPTION_ROUNDED_AVATAR_MAN_BORDER = RequestOptions
            .circleCropTransform()
            .format(DecodeFormat.PREFER_RGB_565)
            .encodeFormat(Bitmap.CompressFormat.WEBP)
            .priority(Priority.HIGH)
            .placeholder(R.drawable.ic_avatar_circle_male_border)
            .error(R.drawable.ic_avatar_circle_male_border)
            .fallback(R.drawable.ic_avatar_circle_male_border)
            .diskCacheStrategy(DiskCacheStrategy.DATA);

    public static final RequestOptions OPTION_ROUNDED_AVATAR_WOMAN = RequestOptions
            .circleCropTransform()
            .format(DecodeFormat.PREFER_RGB_565)
            .encodeFormat(Bitmap.CompressFormat.WEBP)
            .priority(Priority.HIGH)
            .placeholder(R.drawable.ic_avatar_circle_female)
            .error(R.drawable.ic_avatar_circle_female)
            .fallback(R.drawable.ic_avatar_circle_female)
            .diskCacheStrategy(DiskCacheStrategy.DATA);

    public static final RequestOptions OPTION_ROUNDED_AVATAR_MAN = RequestOptions
            .circleCropTransform()
            .format(DecodeFormat.PREFER_RGB_565)
            .encodeFormat(Bitmap.CompressFormat.WEBP)
            .priority(Priority.HIGH)
            .placeholder(R.drawable.ic_avatar_circle_male)
            .error(R.drawable.ic_avatar_circle_male)
            .fallback(R.drawable.ic_avatar_circle_male)
            .diskCacheStrategy(DiskCacheStrategy.DATA);

    public static final RequestOptions OPTION_ROUNDED = RequestOptions
            .circleCropTransform()
            .format(DecodeFormat.PREFER_RGB_565)
            .encodeFormat(Bitmap.CompressFormat.WEBP)
            .priority(Priority.HIGH)
            .placeholder(R.color.default_image_loading)
            .error(R.color.default_image_loading)
            .fallback(R.color.default_image_loading)
            .diskCacheStrategy(DiskCacheStrategy.DATA);

    public static final RequestOptions OPTION_DEFAULT = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .encodeFormat(Bitmap.CompressFormat.WEBP)
            .placeholder(R.color.default_image_loading)
            .error(R.color.default_image_loading)
            .fallback(R.color.default_image_loading)
            .diskCacheStrategy(DiskCacheStrategy.DATA);

    public static final RequestOptions CLEAN_BLACK_OPTION = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .transform(new CleanBackgroundTransform())
            .placeholder(R.color.default_image_loading)
            .error(R.color.default_image_loading)
            .fallback(R.color.default_image_loading)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

    public static final RequestOptions OPTION_BLUR = new RequestOptions()
            .transform(new BlurTransformation());

    public static final RequestOptions DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)
            .override(AppController.SCREEN_WIDTH, AppController.SCREEN_WIDTH);

    //------------------------------------------------------------------//
    //--------------------- Load url from server -----------------------//
    //------------------------------------------------------------------//

    public static void loadImageSend(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(OPTIONS_IMG_CHAT_DEFAULT)
                    .into(imageView);
    }

    public static void loadSticker(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .into(imageView);
    }

    public static void loadImage(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(OPTION_DEFAULT)
                    .into(imageView);
    }

    public static void loadImageForceFirstFrameGif(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(OPTION_DEFAULT)
                    .into(imageView);
    }

    public static void loadImageSimple(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(OPTION_DEFAULT)
                    .into(imageView);
    }

    /**
     * @param url
     * @param view
     */
    public static void loadRoundedAvatar(Context context, String url, int gender, NotificationTarget view) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .thumbnail(THUMBNAIL)
                .apply(gender == Constants.GENDER_TYPE_MAN ? OPTION_ROUNDED_AVATAR_MAN : OPTION_ROUNDED_AVATAR_WOMAN)
                .into(view);
    }

    /**
     * @param url
     * @param gender
     * @param isBorder
     * @param view
     */
    public static void loadRoundedAvatar(String url, int gender, boolean isBorder, ImageView view) {
        Context context = view.getContext();
        if (url == null) {
            Glide.with(context).clear(view);
            view.setImageResource(isBorder ?
                    (gender == Constants.GENDER_TYPE_MAN
                            ? R.drawable.ic_avatar_circle_male_border
                            : R.drawable.ic_avatar_circle_female_border)
                    : (gender == Constants.GENDER_TYPE_MAN
                    ? R.drawable.ic_avatar_circle_male
                    : R.drawable.ic_avatar_circle_female));
        } else {
            RequestOptions requestOptions = isBorder
                    ? (gender == Constants.GENDER_TYPE_MAN ? OPTION_ROUNDED_AVATAR_MAN_BORDER : OPTION_ROUNDED_AVATAR_WOMAN_BORDER)
                    : (gender == Constants.GENDER_TYPE_MAN ? OPTION_ROUNDED_AVATAR_MAN : OPTION_ROUNDED_AVATAR_WOMAN);
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(requestOptions)
                    .into(view);
        }
    }

    /**
     * @param url
     * @param gender
     * @param imageView
     */
    public static void loadRoundedAvatar(String url, int gender, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(gender == Constants.GENDER_TYPE_MAN
                    ? R.drawable.ic_avatar_circle_male_border
                    : R.drawable.ic_avatar_circle_female_border);
        } else
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(gender == Constants.GENDER_TYPE_MAN ? OPTION_ROUNDED_AVATAR_MAN_BORDER : OPTION_ROUNDED_AVATAR_WOMAN_BORDER)
                    .into(imageView);
    }

    /**
     * load round avatar
     *
     * @param url           path or url to load
     * @param mChipListener ChipListener
     */
    public static void loadRoundedAvatar(String url, final ImageView imageView, int gender, final ChipListener mChipListener) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(gender == Constants.GENDER_TYPE_MAN
                    ? R.drawable.ic_avatar_circle_male_border
                    : R.drawable.ic_avatar_circle_female_border);
        } else
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(gender == Constants.GENDER_TYPE_MAN ? OPTION_ROUNDED_AVATAR_MAN_BORDER : OPTION_ROUNDED_AVATAR_WOMAN_BORDER)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mChipListener.onLoadFailed(imageView);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mChipListener.onResourceReady(resource, imageView);
                            return false;
                        }
                    })
                    .into(imageView);
    }

    public static void loadImageProfile(String url, final ImageView imageView) {
        loadImage(url, OPTION_DEFAULT, imageView);
    }

    public static void loadImageFillWidth(String url, ImageView imageView) {
        loadImage(url, ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN, imageView);
    }

    public static void loadImageFillWidthBlur(String url, ImageView imageView) {
        loadImageBlur(url, ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN, imageView);
    }

    public static void loadImageBlur(String url, RequestOptions options, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(imageView.getContext())
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(OPTION_BLUR)
                    .apply(OPTION_DEFAULT)
                    .apply(options)
                    .into(imageView);
    }

    public static void loadImageBlur(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(OPTION_BLUR)
                    .apply(OPTION_DEFAULT)
                    .into(imageView);
    }

    public static void loadImage(String url, RequestOptions options, ImageView imageView) {
        Context context = imageView.getContext();
        if (url == null) {
            Glide.with(context).clear(imageView);
            imageView.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(imageView.getContext())
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(OPTION_DEFAULT)
                    .apply(options)
                    .into(imageView);
    }

    public static void loadBlurImageBanner(final Context context, int width, int height, String url, final ImageView banner) {
        if (url == null) {
            Glide.with(context).clear(banner);
            banner.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(context)
                    .asDrawable()
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(OPTION_DEFAULT)
                    .apply(new RequestOptions().override(width, height).transform(new BlurTransformation()))
                    .into(banner);
    }

    public static void loadCoverImage(final Context context, String url, final ImageView cover) {
        if (url == null) {
            Glide.with(context).clear(cover);
            cover.setImageResource(R.color.default_image_loading);
        } else
            Glide.with(context)
                    .load(url)
                    .thumbnail(THUMBNAIL)
                    .apply(ImagesUtils.OPTION_DEFAULT)
                    .apply(ImagesUtils.OPTION_BLUR)
                    .into(cover);
    }

    //------------------------------------------------------------------------------//
    //------------------------------- Load url local -------------------------------//
    //------------------------------------------------------------------------------//

    public static void loadImageLocal(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .thumbnail(THUMBNAIL)
                .apply(OPTION_DEFAULT)
                .into(imageView);
    }

    public static void loadImage(int idResource, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context).clear(imageView);
        Glide.with(context)
                .load(idResource)
                .thumbnail(THUMBNAIL)
                .apply(OPTION_DEFAULT)
                .into(imageView);
    }

    //------------------------------------------------------------------------------//
    //------------------------- Media server live stream ---------------------------//
    //-----------------------------------------------------------------------------//

    public static void loadImageLiveStreamFillWidth(String url, ImageView imageView) {
        loadImageLiveStream(url, ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN, imageView);
    }

    public static void loadImageLiveStreamFillWidthBlur(String url, ImageView imageView) {
        loadImageLiveStream(url, ImagesUtils.DOWNLOAD_ONLY_OPTIONS_FULL_SCREEN, imageView);
    }

    public static void loadImageLiveStream(String url, RequestOptions options, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context).clear(imageView);
        Glide.with(imageView.getContext())
                .load(url)
                .thumbnail(THUMBNAIL)
                .apply(OPTION_DEFAULT)
                .apply(options)
                .into(imageView);
    }

    public static void loadImageLiveStream(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context).clear(imageView);
        Glide.with(context)
                .load(url)
                .thumbnail(THUMBNAIL)
                .apply(OPTION_DEFAULT)
                .into(imageView);
    }

    //------------------------------------------------------------------------------//
    //------------------------- other functions  -----------------------------------//
    //-----------------------------------------------------------------------------//

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        while (width / 2 >= reqWidth || height / 2 >= reqHeight) {
            width /= 2;
            height /= 2;
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    /**
     * load image fix issue black transparent on kitkat
     * http://10.64.100.201/issues/10030#note-73
     */
    public static void loadImageWithoutBackgroundFix(Context context, String link, ImageView img) {
        Glide
                .with(context)
                .asBitmap()
                .load(link)
                .apply(CLEAN_BLACK_OPTION)
                .into(img);

    }
}

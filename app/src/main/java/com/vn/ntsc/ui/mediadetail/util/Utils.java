package com.vn.ntsc.ui.mediadetail.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class Utils {
    /*
     * var
     * */

    public static final int TIME_DELAY = 2000;
    public static final int REQUEST_CODE_PERMISSION = 1;
    public static final int REQUEST_CODE_DETAIL_MEDIA = 2;

    public static final String INTENT_CODE_DETAIL_MEDIA_POSITION_PLAY_NOW = "INTENT_CODE_DETAIL_MEDIA_POSITION_PLAY_NOW";
    public static final String ARG_DATA_MEDIA = "ARG_DATA_MEDIA";
    public static final String ARG_POSITION_MEDIA = "ARG_POSITION_MEDIA";
    public static final String ARG_PAGE_CHANGE_LISTENER = "ARG_PAGE_CHANGE_LISTENER";
    public static final String CURRENT_POS_PLAY_MEDIA = "CURRENT_POS_PLAY_MEDIA";
    public static final String COMMAND_PAUSE_MUSIC_SYSTEM = "com.android.music.musicservicecommand";
    public static final String COMMAND_NAME = "command";
    public static final String VALUE_NAME = "pause";

    //number page load limit of view pager
    public static final int PAGE_LIMIT = 10;

    //min opacity dim view
    public static final float MIN_OPACITY = 0.2f;

    private static int mMaxTextureSize = 0;

    public static void runAnimationView(Activity activity, @NonNull final View view, int idAnim, @IntRange(from = 0) int timeDelayAmim) throws Exception {
        final Animation animation = AnimationUtils.loadAnimation(view.getContext(), idAnim);
        if (timeDelayAmim > 0)
            animation.setDuration(idAnim);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(animation);
            }
        });
    }

    public static boolean checkPermissionApp(Activity activity, String[] permissionList) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        boolean isOK = false;
        Context context = activity.getApplicationContext();
        for (String permission :
                permissionList) {
            isOK = isOK && (checkSelfPermission(context, permission) != PERMISSION_GRANTED);
        }

        if (!isOK) {
            requestPermissions(activity, permissionList, REQUEST_CODE_PERMISSION);
            return false;
        }
        return true;
    }

    public static boolean checkStatusPermission(int[] grantResults, String[] permissionList) {
        //check result grant permission
        if (grantResults.length == 0)
            return false;

        boolean isOK = true;
        for (int indexGrantResult :
                grantResults) {
            if (indexGrantResult != PERMISSION_GRANTED) {
                isOK = false;
                break;
            }
        }

        return isOK;
    }

    public static String getTagLog(Class<?> classz) {
        if (classz == null)
            return "Classs zzzz!";

        return classz.getSimpleName();
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isAudioFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("audio");
    }


    public static Bitmap getThumbnailSoundFile(File file) {
        Bitmap bitmap = null;

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getAbsolutePath());
            byte[] art = retriever.getEmbeddedPicture();

            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            }
            retriever.release();

            return bitmap;
        } catch (Exception e) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            System.gc();
            return null;
        } catch (OutOfMemoryError o) {
            System.gc();
            return null;
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(File file,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static RelativeLayout.LayoutParams copy(ViewGroup.LayoutParams viewLayoutParamsToCopy) {
        RelativeLayout.LayoutParams copiedParams = new RelativeLayout.LayoutParams(viewLayoutParamsToCopy);
        if (viewLayoutParamsToCopy instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams relativeLayoutParamsToCopy = (RelativeLayout.LayoutParams) viewLayoutParamsToCopy;
            int[] rulesToCopy = relativeLayoutParamsToCopy.getRules();
            for (int verb = 0; verb < rulesToCopy.length; verb++) {
                int subject = rulesToCopy[verb];
                copiedParams.addRule(verb, subject);
            }
        }
        return copiedParams;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * calculating percentage
     *
     * @param currentDuration
     * @param totalDuration
     * @return
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }


    /**
     * @param view
     * @param enabled
     */
    private void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if (view.isScrollContainer()) {
            view.setScrollbarFadingEnabled(enabled);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public static void setSystemUiVisibility(@NonNull View decorView, final ITaskDoAfterSystemUiVisibilityChange task, boolean visible) {
        int newVis = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        if (!visible) {
            newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        decorView.setSystemUiVisibility(newVis);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(final int visibility) {
                int víibilityLowProfile = (visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE);

                if (víibilityLowProfile == 0) {
                    task.whenShow();
                }
            }
        });
    }

    private static int getMaxTextureSize() {
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }

        // Release
        egl.eglTerminate(display);

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * get Maximum allowed width & height of bitmap can load of OpenGLRenderer
     *
     * @return
     */
    public static int getmMaxTextureSize() {
        if (mMaxTextureSize == 0) {
            mMaxTextureSize = getMaxTextureSize();
        }
        return mMaxTextureSize;

    }

    public static Bitmap scaleBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= maxSize && height <= maxSize)
            return image;
        else {
            int max = Math.max(width, height);
            float ratioScale = (max > maxSize) ? (float) maxSize / max : (float) max / maxSize;

            int finalWidth = (int) (width * ratioScale);
            int finalHeight = (int) (height * ratioScale);

            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        }
    }

    public static int[] getSizeBitmapScale(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] result = new int[]{2};
        if (width <= maxSize && height <= maxSize) {
            result[0] = width;
            result[1] = height;
            return result;
        } else {
            int max = Math.max(width, height);
            float ratioScale = (max > maxSize) ? (float) maxSize / max : (float) max / maxSize;

            int finalWidth = (int) (width * ratioScale);
            int finalHeight = (int) (height * ratioScale);

            result[0] = finalWidth;
            result[1] = finalHeight;
            return result;
        }
    }

    /**
     * //get half device height to min opacity when fade view
     *
     * @return
     */
    public static int getScreenHeightScreen(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public interface ITaskDoAfterSystemUiVisibilityChange {
        void whenShow();

        void whenHilde();
    }
}

package com.vn.ntsc.widget.views.livestream;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.vn.ntsc.utils.NativeLibrary;
import com.vn.ntsc.widget.livestream.face.FaceDetectHandler;
import com.vn.ntsc.widget.livestream.face.FaceDetecting;
import com.vn.ntsc.widget.livestream.face.FaceInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.OpenGlUtils;
import jp.co.cyberagent.android.gpuimage.Rotation;
import jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil;

import static javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_DEFAULT_DISPLAY;
import static javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_HEIGHT;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT;
import static javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_WIDTH;
import static javax.microedition.khronos.opengles.GL10.GL_RGBA;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;
import static jp.co.cyberagent.android.gpuimage.OpenGlUtils.NO_TEXTURE;
import static jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil.TEXTURE_NO_ROTATION;

/**
 * カメラから取得した映像へのフィルタ適用を実行するクラス。
 * <p>
 * Created by kuroki on 2017/06/14.
 */
public class VideoFilterRenderer {

    private static final String TAG = "VideoFilterRenderer";

    // GLの頂点バッファ
    static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
            };

    // GL関連
    private EGL10 mEGL;
    private EGLDisplay mEGLDisplay;
    private EGLConfig[] mEGLConfigs;
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;
    private EGLSurface mEGLSurface;
    private GL10 mGL;

    public int mGLTextureId = OpenGlUtils.NO_TEXTURE;
    private FloatBuffer mGLCubeBuffer;  // 頂点バッファ
    private FloatBuffer mGLTextureBuffer;   // テクスチャバッファ
    private IntBuffer   mGLRgbBuffer;
    private IntBuffer   mGLPixelBuffer;
    private int[]       mPixels;
    private byte[]      mYuv;

    private int mOutputWidth;   // 画面への出力サイズ 幅
    private int mOutputHeight;  // 画面への出力サイズ 高さ
    private int mImageWidth;    // フレーム画像のサイズ 幅
    private int mImageHeight;   // フレーム画像のサイズ 高さ

    private Rotation mRotation;
    private boolean  mFlipHorizontal;
    private boolean  mFlipVertical;
    private GPUImage.ScaleType mScaleType = GPUImage.ScaleType.CENTER_INSIDE;

    private Handler mCameraThreadHandler;

    public final Object mSurfaceChangedWaiter = new Object();

    private GPUImageFilter mFilter;

    private SparseArray<FaceInfo> mFaces;
    private FaceDetectHandler mFaceDetectHandler;

    public VideoFilterRenderer(Context applicationContext, Handler mCameraThreadHandler, int width, int height) {

        this.mCameraThreadHandler = mCameraThreadHandler;
        this.mOutputWidth = width;
        this.mOutputHeight = height;

        // 不正なスレッドから生成されていないことをチェック
        this.checkIsOnCameraThread();

        // OpenGL関連の初期化
        initEgl(width, height);

        // 顔検出用ハンドラーの生成
        this.mFaceDetectHandler = new FaceDetectHandler(applicationContext);

        setRotation(Rotation.NORMAL, false, false);
    }

    private void initEgl(int width, int height) {

        // No error checking performed, minimum required code to elucidate logic
        mEGL = (EGL10) EGLContext.getEGL();
        mEGLDisplay = mEGL.eglGetDisplay(EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        mEGL.eglInitialize(mEGLDisplay, version);
        mEGLConfig = chooseConfig(); // Choosing a config is a little more
        // complicated

        int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        int[] attrib_list = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        mEGLContext = mEGL.eglCreateContext(mEGLDisplay, mEGLConfig, EGL_NO_CONTEXT, attrib_list);

        int[] attribList = new int[]{
                EGL_WIDTH, width,
                EGL_HEIGHT, height,
                EGL_NONE
        };
        mEGLSurface = mEGL.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, attribList);
        mEGL.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);

        mGL = (GL10) mEGLContext.getGL();


        // 頂点バッファ、テクスチャバッファを生成しておく
        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    /**
     * 指定されたフィルタを設定します。
     *
     * @param filter 適用するフィルタオブジェクト
     */
    public void setFilter(GPUImageFilter filter) {

        if (mFilter != null) {
            mFilter.destroy();
            mFilter = null;
        }
        mFilter = filter;
    }

    /**
     * 適用中のフィルタを返却します。
     *
     * @return 適用中のフィルタ
     */
    public GPUImageFilter getFilter() {
        return this.mFilter;
    }

    /**
     * フレーム画像にフィルタを適用するメソッド
     *
     * @param data             フレーム画像データ (YUV形式)
     * @param imageWidth       フレーム画像サイズ 幅
     * @param imageHeight      フレーム画像サイズ 高さ
     * @param frameOrientation
     * @return フィルタ適用後のフレームデータ (YUV形式)
     */
    public byte[] filter(byte[] data, int imageWidth, int imageHeight, int frameOrientation) {

        // フレーム画像サイズを退避しておく
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;

        synchronized (mSurfaceChangedWaiter) {
            mSurfaceChangedWaiter.notifyAll();
        }

        // Filterの初期化
        if (!mFilter.isInitialized()) {
            mFilter.init();
        }

        // 顔認識が必要な場合
        Log.i(TAG, "mFilter:" + mFilter.getClass().getSimpleName());
        if (mFilter instanceof FaceDetecting) {
            if (!mFaceDetectHandler.isRunning()) {
                mFaceDetectHandler.start();
            }

            // 顔認識処理
            mFaceDetectHandler.setData(data, imageWidth, imageHeight);   // フレームを設定
            mFaces = mFaceDetectHandler.getFaces();   // 検出結果の取得
            ((FaceDetecting) mFilter).setFaces(mFaces);  // 検出結果をフィルタに設定

        } else {
            // 顔認識が不要な場合は止める
            if (mFaceDetectHandler.isRunning()) {
                mFaceDetectHandler.stop();
            }
            mFaces = null;
        }

        // フレーム画像の変換 (YUV -> RGB変換)
        if (mGLRgbBuffer == null) {
            mGLRgbBuffer = IntBuffer.allocate(imageWidth * imageHeight);
        }
        NativeLibrary.YUVtoARGB(data, imageWidth, imageHeight, mGLRgbBuffer.array());

        // フレーム画像データからテクスチャを生成
        mGLTextureId = loadTexture(mGLRgbBuffer, imageWidth, imageHeight, mGLTextureId);

        adjustImageScaling();


        // === ここからオフスクリーンへの描画処理 ===

        // 画面の出力範囲を設定
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);

        // 画面の初期化
        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // 背景テクスチャの描画とFilterの適用
        mFilter.onDraw(mGLTextureId, mGLCubeBuffer, mGLTextureBuffer);

        // オフスクリーンからピクセルを取得
        if (mGLPixelBuffer == null) {
            mGLPixelBuffer = IntBuffer.allocate(imageWidth * imageHeight);
            mPixels = new int[imageWidth * imageHeight];
            mYuv = new byte[imageWidth * imageHeight * 3 / 2];
        }
        mGLPixelBuffer.position(0);
        mGL.glReadPixels(0, 0, mOutputWidth, mOutputHeight, GL_RGBA, GL_UNSIGNED_BYTE, mGLPixelBuffer);
        int[] iPixels = mGLPixelBuffer.array();
        for (int i = 0; i < mOutputHeight; i++) {
            for (int j = 0; j < mOutputWidth; j++) {
                mPixels[(mOutputHeight - i - 1) * mOutputWidth + j] = iPixels[i * mOutputWidth + j];
            }
        }

        // Pixel -> YUV変換
        NativeLibrary.ARGBtoYUV(mPixels, mOutputWidth, mOutputHeight, mYuv);

        // 後始末
        GLES20.glDeleteTextures(1, new int[]{mGLTextureId}, 0);
        mGLTextureId = OpenGlUtils.NO_TEXTURE;

        return mYuv;
    }

    public void destroy() {

        mEGL.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE,
                            EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);

        mEGL.eglDestroySurface(mEGLDisplay, mEGLSurface);
        mEGL.eglDestroyContext(mEGLDisplay, mEGLContext);
        mEGL.eglTerminate(mEGLDisplay);

        // 顔検出のリリース
        if (mFaceDetectHandler != null) {
            mFaceDetectHandler.release();
            mFaceDetectHandler = null;
        }

        // Filterの後始末
        if (mFilter != null && mFilter.isInitialized()) {
            mFilter.destroy();
        }
    }

    private void adjustImageScaling() {

        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;
        if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
            outputWidth = mOutputHeight;
            outputHeight = mOutputWidth;
        }

        float ratio1 = outputWidth / mImageWidth;
        float ratio2 = outputHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        float[] cube = CUBE;
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, mFlipHorizontal, mFlipVertical);
        if (mScaleType == GPUImage.ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                    addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                    addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                    addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical),
                    };
        } else {
            cube = new float[]{
                    CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                    CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                    CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                    CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,
                    };
        }

        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public void setRotation(final Rotation rotation) {
        mRotation = rotation;
        adjustImageScaling();
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        mFlipHorizontal = flipHorizontal;
        mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    private EGLConfig chooseConfig() {
        int[] attribList = new int[]{
                EGL_DEPTH_SIZE, 0,
                EGL_STENCIL_SIZE, 0,
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL_NONE
        };

        // No error checking performed, minimum required code to elucidate logic
        // Expand on this logic to be more selective in choosing a configuration
        int[] numConfig = new int[1];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, null, 0, numConfig);
        int configSize = numConfig[0];
        mEGLConfigs = new EGLConfig[configSize];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, mEGLConfigs, configSize, numConfig);

        return mEGLConfigs[0]; // Best match is probably the first configuration
    }

    private void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.mCameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }

    public int loadTexture(final IntBuffer data, final int width, final int height, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                                   GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                                   GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                                   GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                                   GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width,
                                   height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

}

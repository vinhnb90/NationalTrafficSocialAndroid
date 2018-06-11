package org.webrtc;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

import com.vn.ntsc.widget.views.livestream.VideoFilterRenderer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static android.graphics.ImageFormat.NV21;

/**
 * WebRTC SDK の Camera1Session のソースをコピー・修正して作成したクラス
 * <p>
 * Created by kuroki on 2017/06/08.
 */

public class Camera3Session implements CameraSession {

    private static final String TAG = "Camera3Session";
    private final Handler cameraThreadHandler;
    private final Events events;
    private final Context applicationContext;
    private final SurfaceTextureHelper surfaceTextureHelper;
    private final int                                    cameraId;
    private final int                                    framerate;
    private final Camera camera;
    private final Camera.CameraInfo                      info;
    private final CameraEnumerationAndroid.CaptureFormat captureFormat;
    private       Camera3Session.SessionState            state;
    private boolean firstFrameReported = false;
    private boolean cameraIsReleased = false;

    private VideoFilterRenderer mVideoFilterRenderer;

    // フィルター
    private GPUImageFilter filter;

    // フレームレートの測定用
    private int  frameCount = 0;
    private long frameTime  = 0;


    public static void create(CreateSessionCallback callback, Events events, Context applicationContext,
                              SurfaceTextureHelper surfaceTextureHelper,
                              int cameraId, int width, int height, int framerate) {
        long constructionTimeNs = System.nanoTime();
        Logging.d("Camera3Session", "Open camera " + cameraId);
        events.onCameraOpening();

        Camera camera;
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException var20) {
            callback.onFailure(FailureType.ERROR, var20.getMessage());
            return;
        }

        try {
            camera.setPreviewTexture(surfaceTextureHelper.getSurfaceTexture());
        } catch (IOException var19) {
            camera.release();
            callback.onFailure(FailureType.ERROR, var19.getMessage());
            return;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        Camera.Parameters parameters = camera.getParameters();
        CameraEnumerationAndroid.CaptureFormat captureFormat = findClosestCaptureFormat(parameters, width, height, framerate);
        Size pictureSize = findClosestPictureSize(parameters, width, height);
        updateCameraParameters(camera, parameters, captureFormat, pictureSize);

        int frameSize = captureFormat.frameSize();

        for (int i = 0; i < 3; ++i) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(frameSize);
            camera.addCallbackBuffer(buffer.array());
        }

        camera.setDisplayOrientation(0);
        callback.onDone(new Camera3Session(events, applicationContext, surfaceTextureHelper,
                                           cameraId, width, height, framerate, camera, info, captureFormat, constructionTimeNs));
    }

    private static void updateCameraParameters(Camera camera, Camera.Parameters parameters,
                                               CameraEnumerationAndroid.CaptureFormat captureFormat, Size pictureSize) {
        List focusModes = parameters.getSupportedFocusModes();
        parameters.setPreviewFpsRange(captureFormat.framerate.min, captureFormat.framerate.max);
        parameters.setPreviewSize(captureFormat.width, captureFormat.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        captureFormat.getClass();
        parameters.setPreviewFormat(NV21);

        if (parameters.isVideoStabilizationSupported()) {
            parameters.setVideoStabilization(true);
        }

        if (focusModes.contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }

        camera.setParameters(parameters);
    }

    private static CameraEnumerationAndroid.CaptureFormat findClosestCaptureFormat(Camera.Parameters parameters,
                                                                                   int width, int height, int framerate) {
        List supportedFramerates = Camera3Enumerator.convertFramerates(parameters.getSupportedPreviewFpsRange());
        Logging.d("Camera3Session", "Available fps ranges: " + supportedFramerates);

        CameraEnumerationAndroid.CaptureFormat.FramerateRange fpsRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(supportedFramerates, framerate);

        Size previewSize = CameraEnumerationAndroid.getClosestSupportedSize(Camera3Enumerator.convertSizes(parameters.getSupportedPreviewSizes()), width, height);

        return new CameraEnumerationAndroid.CaptureFormat(previewSize.width, previewSize.height, fpsRange);
    }

    private static Size findClosestPictureSize(Camera.Parameters parameters, int width, int height) {
        return CameraEnumerationAndroid.getClosestSupportedSize(Camera3Enumerator.convertSizes(parameters.getSupportedPictureSizes()), width, height);
    }

    private Camera3Session(Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper,
                           int cameraId, int width, int height, int framerate,
                           Camera camera, Camera.CameraInfo info, CameraEnumerationAndroid.CaptureFormat captureFormat, long constructionTimeNs) {
        Logging.d("Camera3Session", "Create new camera1 session on camera " + cameraId);
        this.cameraThreadHandler = new Handler();
        this.events = events;
        this.applicationContext = applicationContext;
        this.surfaceTextureHelper = surfaceTextureHelper;
        this.cameraId = cameraId;
        this.framerate = framerate;
        this.camera = camera;
        this.info = info;
        this.captureFormat = captureFormat;

        this.startCapturing();
    }

    public void stop() {
        Logging.d("Camera3Session", "Stop camera1 session on camera " + this.cameraId);
        this.checkIsOnCameraThread();
        if (this.state != Camera3Session.SessionState.STOPPED) {
            this.state = Camera3Session.SessionState.STOPPED;
            this.stopInternal();
        }
        if (mVideoFilterRenderer != null) {
            Log.i(TAG, "mVideoFilterRenderer.destroy()");
            mVideoFilterRenderer.destroy();
            mVideoFilterRenderer = null;
        }
    }

    private void startCapturing() {
        Logging.d("Camera3Session", "Start capturing");
        this.checkIsOnCameraThread();
        this.state = Camera3Session.SessionState.RUNNING;
        this.camera.setErrorCallback(new Camera.ErrorCallback() {
            public void onError(int error, Camera camera) {
                String errorMessage;
                if (error == 100) {
                    errorMessage = "Camera server died!";
                } else {
                    errorMessage = "Camera error: " + error;
                }

                Logging.e("Camera3Session", errorMessage);
                Camera3Session.this.state = Camera3Session.SessionState.STOPPED;
                Camera3Session.this.stopInternal();
                if (error == 2) {
                    Camera3Session.this.events.onCameraDisconnected(Camera3Session.this);
                } else {
                    Camera3Session.this.events.onCameraError(Camera3Session.this, errorMessage);
                }

            }
        });

        // VideoFilterHandlerの生成
        if (mVideoFilterRenderer == null) {
            Log.i(TAG, "new VideoFilterRenderer");
            this.mVideoFilterRenderer = new VideoFilterRenderer(applicationContext, cameraThreadHandler, captureFormat.width, captureFormat.height);
            this.mVideoFilterRenderer.setFilter(filter);
        }

        this.listenForBytebufferFrames();

        try {
            this.camera.startPreview();
        } catch (RuntimeException var2) {
            this.state = Camera3Session.SessionState.STOPPED;
            this.stopInternal();
            this.events.onCameraError(this, var2.getMessage());
        }
    }

    private void stopInternal() {
        Logging.d("Camera3Session", "Stop internal");
        this.checkIsOnCameraThread();
        this.surfaceTextureHelper.stopListening();
        if (!cameraIsReleased) {
            this.camera.stopPreview();
            this.camera.release();
            cameraIsReleased = true;
        }
        this.events.onCameraClosed(this);
        Logging.d("Camera3Session", "Stop done");
    }

    private void listenForBytebufferFrames() {
        Logging.d("Camera3Session", "listenForBytebufferFrames.");
        this.camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera callbackCamera) {
                checkIsOnCameraThread();
                if (callbackCamera != camera) {
                    Logging.e("Camera3Session", "Callback from a different camera. This should never happen.");
                } else if (state != Camera3Session.SessionState.RUNNING) {
                    Logging.d("Camera3Session", "Bytebuffer frame captured but camera is no longer running.");
                } else {
                    long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                    if (!firstFrameReported) {
                        firstFrameReported = true;
                    }

                    if (mVideoFilterRenderer.getFilter() == null) {
                        events.onByteBufferFrameCaptured(Camera3Session.this,
                                                         data, captureFormat.width, captureFormat.height,
                                                         getFrameOrientation(), captureTimeNs);
                        camera.addCallbackBuffer(data);
                    } else {
                        byte[] filteredData = mVideoFilterRenderer.filter(data, captureFormat.width, captureFormat.height, getFrameOrientation());
                        events.onByteBufferFrameCaptured(Camera3Session.this,
                                                         filteredData, captureFormat.width, captureFormat.height,
                                                         getFrameOrientation(), captureTimeNs);
                        camera.addCallbackBuffer(data);
                    }
                }

                // フレームレートを出力(デバッグ用)
                if (frameTime == 0) {
                    frameTime = System.currentTimeMillis();
                } else {
                    frameCount++;
                    long interval = System.currentTimeMillis() - frameTime;
                    if (interval > 1000) {
                        frameTime = System.currentTimeMillis();
                        frameCount = 0;
                    }
                }

            }
        });
    }

    private int getDeviceOrientation() {
        WindowManager wm = (WindowManager) this.applicationContext.getSystemService("window");
        short orientation1;
        switch (wm.getDefaultDisplay().getRotation()) {
            case 0:
            default:
                orientation1 = 0;
                break;
            case 1:
                orientation1 = 90;
                break;
            case 2:
                orientation1 = 180;
                break;
            case 3:
                orientation1 = 270;
        }

        return orientation1;
    }

    private int getFrameOrientation() {
        int rotation = this.getDeviceOrientation();
        if (this.info.facing == 0) {
            rotation = 360 - rotation;
        }

        return (this.info.orientation + rotation) % 360;
    }

    private void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }

    public void setFilter(final GPUImageFilter filter) {
        this.cameraThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Camera3Session.this.filter = filter;
                mVideoFilterRenderer.setFilter(filter);
            }
        });
    }

    private enum SessionState {
        RUNNING,
        STOPPED
    }
}

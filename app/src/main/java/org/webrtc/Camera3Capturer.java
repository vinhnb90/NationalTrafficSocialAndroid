package org.webrtc;

import android.content.Context;
import android.media.MediaRecorder;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * WebRTC SDK の Camera1Capturer のソースをコピー・修正して作成したクラス
 * <p>
 * Created by kuroki on 2017/06/08.
 */
public class Camera3Capturer extends CameraCapturer {

    private Camera3Session cameraSession;

    public Camera3Capturer(String cameraName, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
        super(cameraName, eventsHandler, new Camera3Enumerator());
    }

    public void setFilter(GPUImageFilter filter) {
        this.cameraSession.setFilter(filter);
    }

    @Override
    protected void createCameraSession(final CameraSession.CreateSessionCallback createSessionCallback,
                                       CameraSession.Events events, Context context,
                                       SurfaceTextureHelper surfaceTextureHelper,
                                       MediaRecorder mediaRecorder, String cameraName,
                                       int width, int height, int framerate) {

        Camera3Session.create(new CameraSession.CreateSessionCallback() {
            @Override
            public void onDone(CameraSession cameraSession) {
                Camera3Capturer.this.cameraSession = (Camera3Session) cameraSession;
                createSessionCallback.onDone(cameraSession);
            }

            @Override
            public void onFailure(CameraSession.FailureType failureType, String s) {
                createSessionCallback.onFailure(failureType, s);
            }
        }, events, context, surfaceTextureHelper, Camera3Enumerator.getCameraIndex(cameraName), width, height, framerate);
    }
}

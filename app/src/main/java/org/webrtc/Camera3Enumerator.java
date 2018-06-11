package org.webrtc;

import android.hardware.Camera;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * WebRTC SDK の Camera1Enumerator のソースをコピー・修正して作成したクラス
 * <p>
 * Created by kuroki on 2017/06/08.
 */

public class Camera3Enumerator implements CameraEnumerator {

    private static final String TAG = "Camera3Enumerator";

    private static List<List<CameraEnumerationAndroid.CaptureFormat>> cachedSupportedFormats;

    public String[] getDeviceNames() {
        ArrayList namesList = new ArrayList();

        for (int namesArray = 0; namesArray < Camera.getNumberOfCameras(); ++namesArray) {
            String name = getDeviceName(namesArray);
            if (name != null) {
                namesList.add(name);
                Logging.d("Camera3Enumerator", "Index: " + namesArray + ". " + name);
            } else {
                Logging.e("Camera3Enumerator", "Index: " + namesArray + ". Failed to query camera name.");
            }
        }

        String[] var4 = new String[namesList.size()];
        return (String[]) namesList.toArray(var4);
    }

    public boolean isFrontFacing(String deviceName) {
        Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 1;
    }

    public boolean isBackFacing(String deviceName) {
        Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 0;
    }

    public List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(String deviceName) {
        return getSupportedFormats(getCameraIndex(deviceName));
    }

    public CameraVideoCapturer createCapturer(String deviceName, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
        return new Camera3Capturer(deviceName, eventsHandler);
    }

    private static Camera.CameraInfo getCameraInfo(int index) {
        Camera.CameraInfo info = new Camera.CameraInfo();

        try {
            Camera.getCameraInfo(index, info);
            return info;
        } catch (Exception var3) {
            Logging.e("Camera3Enumerator", "getCameraInfo failed on index " + index, var3);
            return null;
        }
    }

    static synchronized List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(int cameraId) {
        if (cachedSupportedFormats == null) {
            cachedSupportedFormats = new ArrayList();

            for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
                cachedSupportedFormats.add(enumerateFormats(i));
            }
        }
        return (List) cachedSupportedFormats.get(cameraId);
    }

    private static List<CameraEnumerationAndroid.CaptureFormat> enumerateFormats(int cameraId) {
        Logging.d("Camera3Enumerator", "Get supported formats for camera index " + cameraId + ".");
        long startTimeMs = SystemClock.elapsedRealtime();
        Camera camera = null;

        Camera.Parameters parameters;
        label94:
        {
            ArrayList endTimeMs;
            try {
                Logging.d("Camera3Enumerator", "Opening camera with index " + cameraId);
                camera = Camera.open(cameraId);
                parameters = camera.getParameters();
                break label94;
            } catch (RuntimeException var15) {
                Logging.e("Camera3Enumerator", "Open camera failed on camera index " + cameraId, var15);
                endTimeMs = new ArrayList();
            } finally {
                if (camera != null) {
                    camera.release();
                }
            }
            return endTimeMs;
        }

        ArrayList formatList = new ArrayList();

        try {
            int endTimeMs1 = 0;
            int maxFps = 0;
            List listFpsRange = parameters.getSupportedPreviewFpsRange();
            if (listFpsRange != null) {
                int[] i$ = (int[]) listFpsRange.get(listFpsRange.size() - 1);
                endTimeMs1 = i$[0];
                maxFps = i$[1];
            }

            Iterator i$1 = parameters.getSupportedPreviewSizes().iterator();

            while (i$1.hasNext()) {
                Camera.Size size = (Camera.Size) i$1.next();
                formatList.add(new CameraEnumerationAndroid.CaptureFormat(size.width, size.height, endTimeMs1, maxFps));
            }
        } catch (Exception var14) {
            Logging.e("Camera3Enumerator", "getSupportedFormats() failed on camera index " + cameraId, var14);
        }

        long endTimeMs2 = SystemClock.elapsedRealtime();
        Logging.d("Camera3Enumerator", "Get supported formats for camera index " + cameraId + " done." + " Time spent: " + (endTimeMs2 - startTimeMs) + " ms.");
        return formatList;
    }

    static List<org.webrtc.Size> convertSizes(List<Camera.Size> cameraSizes) {
        ArrayList sizes = new ArrayList();
        Iterator i$ = cameraSizes.iterator();

        while (i$.hasNext()) {
            Camera.Size size = (Camera.Size) i$.next();
            sizes.add(new org.webrtc.Size(size.width, size.height));
        }

        return sizes;
    }

    static List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> convertFramerates(List<int[]> arrayRanges) {
        ArrayList ranges = new ArrayList();
        Iterator i$ = arrayRanges.iterator();

        while (i$.hasNext()) {
            int[] range = (int[]) i$.next();
            ranges.add(new CameraEnumerationAndroid.CaptureFormat.FramerateRange(range[0], range[1]));
        }

        return ranges;
    }

    static int getCameraIndex(String deviceName) {
        Logging.d("Camera3Enumerator", "getCameraIndex: " + deviceName);

        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            if (deviceName.equals(getDeviceName(i))) {
                return i;
            }
        }

        throw new IllegalArgumentException("No such camera: " + deviceName);
    }

    static String getDeviceName(int index) {
        Camera.CameraInfo info = getCameraInfo(index);
        if (info == null) {
            return null;
        } else {
            String facing = info.facing == 1 ? "front" : "back";
            return "Camera " + index + ", Facing " + facing + ", Orientation " + info.orientation;
        }
    }
}

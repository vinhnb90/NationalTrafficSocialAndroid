package com.vn.ntsc.utils;

/**
 * Native
 */

public class NativeLibrary {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void YUVtoARGB(byte[] yuv, int width, int height, int[] out);

    public static native void ARGBtoYUV(int[] argb, int width, int height, byte[] out);
}

package com.vn.ntsc.ui.mediadetail.custom;

public class Cubic {

    /**
     * is a Cubic Bezier to calculate ratio aspect when move scroll
     * @param time
     * @param start
     * @param end
     * @param duration
     * @return
     */
    public static float easeOut(float time, float start, float end, float duration) {
        return end * ((time = time / duration - 1) * time * time + 1) + start;
    }
}
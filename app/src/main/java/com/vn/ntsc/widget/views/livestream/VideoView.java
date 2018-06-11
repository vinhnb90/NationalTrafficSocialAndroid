package com.vn.ntsc.widget.views.livestream;

import android.content.Context;
import android.util.AttributeSet;

import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

/**
 * Created by nankai on 12/11/2017.
 */

public class VideoView extends SurfaceViewRenderer {

    public enum ScalingType {
        SCALE_ASPECT_FIT,
        SCALE_ASPECT_FILL,
        SCALE_ASPECT_BALANCED
    }

    private VideoRenderer videoRenderer;

    private MediaStream mediaStream;

    public VideoView(Context context) {
        super(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(EglBase eglBase) {

        if (videoRenderer == null) {
            videoRenderer = new VideoRenderer(this);
        }

        init(eglBase.getEglBaseContext(), null);
        setZOrderMediaOverlay(true);
        setEnableHardwareScaler(true /* enabled */);
    }

    public void attach(MediaStream stream) {
        this.mediaStream = stream;
        final org.webrtc.MediaStream rtcStream = stream.rtcMediaStream();
        if (!rtcStream.videoTracks.isEmpty()) {
            VideoTrack localVideoTrack = rtcStream.videoTracks.getLast();
            localVideoTrack.setEnabled(true);
            localVideoTrack.addRenderer(videoRenderer);
        }
    }

    public void stopRender() {
        final org.webrtc.MediaStream rtcStream = this.mediaStream.rtcMediaStream();
        if (!rtcStream.videoTracks.isEmpty()) {
            final VideoTrack videoTrack = rtcStream.videoTracks.getLast();
            videoTrack.setEnabled(false);
            videoTrack.removeRenderer(videoRenderer);
        }
    }

    public void setScalingType(VideoView.ScalingType scalingType) {
        org.webrtc.RendererCommon.ScalingType rtcScalingType = org.webrtc.RendererCommon.ScalingType.valueOf(scalingType.toString());
        super.setScalingType(rtcScalingType);
    }

}

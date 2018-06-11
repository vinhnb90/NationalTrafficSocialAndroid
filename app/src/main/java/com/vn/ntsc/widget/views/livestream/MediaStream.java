package com.vn.ntsc.widget.views.livestream;

/**
 * Created by kuroki on 2017/09/18.
 */

public class MediaStream {

    private org.webrtc.MediaStream mediaStream;

    private String label;

    private boolean isFrontFacing;

    public MediaStream(org.webrtc.MediaStream mediaStream) {
        this.mediaStream = mediaStream;
        this.label = mediaStream.label();
    }

    public MediaStream(String label) {
        this.label = label;
    }

    public org.webrtc.MediaStream rtcMediaStream() {
        return mediaStream;
    }

    public String label() {
        return label;
    }

    public void setFrontFacing(boolean frontFacing) {
        isFrontFacing = frontFacing;
    }

    public boolean isFrontFacing() {
        return isFrontFacing;
    }
}

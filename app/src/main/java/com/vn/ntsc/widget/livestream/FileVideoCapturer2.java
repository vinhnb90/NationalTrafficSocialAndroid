package com.vn.ntsc.widget.livestream;

import android.content.Context;
import android.os.SystemClock;

import com.vn.ntsc.utils.LogUtils;

import org.webrtc.FileVideoCapturer;
import org.webrtc.Logging;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by nankai on 3/8/2018.
 */

public class FileVideoCapturer2 implements VideoCapturer {
    private static final String TAG = "FileVideoCapturer";

    private final FileVideoCapturer2.VideoReader videoReader;

    private CapturerObserver capturerObserver;
    private final Timer timer = new Timer();
    private final TimerTask tickTask = new TimerTask() {
        public void run() {
            FileVideoCapturer2.this.tick();
        }
    };

    private int getFrameWidth() {
        return this.videoReader.getFrameWidth();
    }

    private int getFrameHeight() {
        return this.videoReader.getFrameHeight();
    }

    public FileVideoCapturer2(String inputFile) throws IOException {
        try {
            this.videoReader = new FileVideoCapturer2.VideoReaderMedia(inputFile);
        } catch (IOException var3) {
            Logging.d("FileVideoCapturer", "Could not open video file: " + inputFile);
            throw var3;
        }
    }

    private byte[] getNextFrame() {
        return this.videoReader.getNextFrame();
    }

    private void tick() {
        long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
        byte[] frameData = this.getNextFrame();
        this.capturerObserver.onByteBufferFrameCaptured(frameData, this.getFrameWidth(), this.getFrameHeight(), 0, captureTimeNs);
    }

    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
        this.capturerObserver = capturerObserver;
    }

    public void startCapture(int width, int height, int framerate) {
        this.timer.schedule(this.tickTask, 0L, (long)(1000 / framerate));
    }

    public void stopCapture() throws InterruptedException {
        this.timer.cancel();
    }

    public void changeCaptureFormat(int width, int height, int framerate) {
    }

    public void dispose() {
        this.videoReader.close();
    }

    public boolean isScreencast() {
        return false;
    }

    private static class VideoReaderMedia implements FileVideoCapturer2.VideoReader {

        private static final String TAG = "VideoReaderY4M";
        private final int frameWidth;
        private final int frameHeight;
        private final int frameSize;
        private final long videoStart;
        private final RandomAccessFile mediaFileStream;

        VideoReaderMedia(String path) throws IOException {
            this.mediaFileStream = new RandomAccessFile(new File(path), "r");
            StringBuilder builder = new StringBuilder();

            while(true) {
                int c = this.mediaFileStream.read();
                if(c == -1) {
                    throw new RuntimeException("Found end of file before end of header for file: " + path);
                }

                if(c == 10) {
                    this.videoStart = this.mediaFileStream.getFilePointer();
                    String header = builder.toString();
                    String[] headerTokens = header.split("[ ]");
                    int w = 0;
                    int h = 0;

                    for (String tok : headerTokens) {
                        LogUtils.i(TAG, tok);
                        char m = tok.charAt(0);
                        switch (m) {
                            case 'H':
                                h = Integer.parseInt(tok.substring(1));
                                break;
                            case 'W':
                                w = Integer.parseInt(tok.substring(1));
                        }
                    }

                    if(w % 2 != 1 && h % 2 != 1) {
                        this.frameWidth = w;
                        this.frameHeight = h;
                        this.frameSize = w * h * 3 / 2;
                        Logging.d("VideoReaderY4M", "frame dim: (" + w + ", " + h + ") frameSize: " + this.frameSize);
                        return;
                    }

                    throw new IllegalArgumentException("Does not support odd width or height");
                }

                builder.append((char)c);
            }
        }

        @Override
        public int getFrameWidth() {
            return this.frameWidth;
        }

        @Override
        public int getFrameHeight() {
            return this.frameHeight;
        }

        @Override
        public byte[] getNextFrame() {
            byte[] frame = new byte[this.frameSize];

            try {
                byte[] frameDeli = new byte["FRAME".length() + 1];
                if(this.mediaFileStream.read(frameDeli) < frameDeli.length) {
                    this.mediaFileStream.seek(this.videoStart);
                    if(this.mediaFileStream.read(frameDeli) < frameDeli.length) {
                        throw new RuntimeException("Error looping video");
                    }
                }

                String frameDelimStr = new String(frameDeli);
                if(!frameDelimStr.equals("FRAME\n")) {
                    throw new RuntimeException("Frames should be delimited by FRAME plus newline, found delimter was: '" + frameDelimStr + "'");
                } else {
                    this.mediaFileStream.readFully(frame);
                    byte[] nv21Frame = new byte[this.frameSize];
                    FileVideoCapturer.nativeI420ToNV21(frame, this.frameWidth, this.frameHeight, nv21Frame);
                    return nv21Frame;
                }
            } catch (IOException var5) {
                throw new RuntimeException(var5);
            }
        }

        public void close() {
            try {
                this.mediaFileStream.close();
            } catch (IOException var2) {
                Logging.e("VideoReaderY4M", "Problem closing file", var2);
            }

        }
    }

    private interface VideoReader {
        int getFrameWidth();

        int getFrameHeight();

        byte[] getNextFrame();

        void close();
    }
}

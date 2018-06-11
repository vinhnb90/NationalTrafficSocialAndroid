package com.vn.ntsc.services.upload;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by nankai on 11/21/2017.
 */

public class ProgressRequestBody extends RequestBody {


    protected RequestBody mDelegate;
    private UploadCallbacks mListener;
    protected CountingSink mCountingSink;
    // to avoid duplicated notification
    private int mLastProgress;
    private boolean isSuccess = false;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);

        void onError();

        void onFinish(int index);
    }

    public ProgressRequestBody(RequestBody delegate, final UploadCallbacks listener) {
        mDelegate = delegate;
        mListener = listener;
    }


    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        try {
            return mDelegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        mCountingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(mCountingSink);
        mDelegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            int pogress = (int) (100F * bytesWritten / contentLength());

            if (mLastProgress != pogress && !isSuccess) {
                mLastProgress = pogress;
                int notifyProgress = (int) (100F * bytesWritten / contentLength());
                if (notifyProgress == 100) {
                    isSuccess = true;
                }

                mListener.onProgressUpdate(notifyProgress);
            }
        }
    }
}



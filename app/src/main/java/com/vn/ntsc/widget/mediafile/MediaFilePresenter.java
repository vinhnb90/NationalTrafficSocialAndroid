package com.vn.ntsc.widget.mediafile;

import android.content.ContentResolver;
import android.database.Cursor;

import com.vn.ntsc.repository.media.MediaFileRepository;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.mediafile.rx.RxCursorLoader;

import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Created by nankai on 2017 Sep 17
 */
public class MediaFilePresenter implements MediaFileContract.Presenter {

    private final MediaFileContract.View view;
    private CompositeDisposable disposes;

    public MediaFilePresenter(MediaFileContract.View view) {
        this.view = view;
        disposes = new CompositeDisposable();
    }

    @Override
    public void onExecuteGetMediaFileReady(final int mediaType, int limit, int offset, ContentResolver contentResolver) {
        LogUtils.i("MediaFilePresenter","onExecuteGetMediaFileReady -> mediaType : " + mediaType);
        RxCursorLoader.Query query;
        if (mediaType == MediaFileRepository.AUDIO_LOADER_ID_MODE) {
            query = MediaFileRepository.getAudio(limit, offset);
        } else if (mediaType == MediaFileRepository.IMAGE_LOADER_ID_MODE) {
            query = MediaFileRepository.getImage(limit, offset);
        } else if (mediaType == MediaFileRepository.VIDEO_LOADER_ID_MODE) {
            query = MediaFileRepository.getVideo(limit, offset);
        } else {
            query = MediaFileRepository.getMediaFile(limit, offset);
        }

        Disposable mCursorDisposable = RxCursorLoader
                .flowable(contentResolver, query, Schedulers.io(), BackpressureStrategy.LATEST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) throws Exception {
                        LogUtils.i("MediaFilePresenter","accept-> mediaType : " + mediaType);
                        view.onCursorLoaded(cursor, mediaType);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.onCursorLoadFailed(throwable, mediaType);
                    }
                });
        disposes.add(mCursorDisposable);
    }

    @Override
    public void destroy() {
        if (disposes != null) {
            disposes.dispose();
            disposes = null;
        }
    }
}

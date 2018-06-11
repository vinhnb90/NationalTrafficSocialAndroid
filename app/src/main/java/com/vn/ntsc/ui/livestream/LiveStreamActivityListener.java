package com.vn.ntsc.ui.livestream;

import com.vn.ntsc.repository.model.timeline.datas.sub.ListCommentBean;

import io.reactivex.subjects.Subject;

/**
 * Created by nankai on 12/7/2017.
 */

public interface LiveStreamActivityListener {

    enum LiveStreamState {
        OPEN, PREPARE, JOINED, CLOSE, ERROR, REFRESH
    }

    /**
     *
     * @return Disposable
     */
    Subject<LiveStreamState>  getLiveStreamStateDisposable();

    /**
     *
     * @return Disposable
     */
    Subject<Integer> getViewNumberDisposable();

    /**
     *
     * @return Disposable
     */
    Subject<ListCommentBean> getSocketDisposable();

    /**
     * state create live stream
     *
     * @return
     */
    LiveStreamState getState();

    /**
     * replace state create live stream
     *
     * @param state
     * @return
     */
    LiveStreamState setState(LiveStreamState state);

    /**
     * change front facing camera
     */
    void changeCamera();

    /**
     * Connect live stream
     */
    void connect();

    /**
     * leave live stream
     */
    void leave();
}

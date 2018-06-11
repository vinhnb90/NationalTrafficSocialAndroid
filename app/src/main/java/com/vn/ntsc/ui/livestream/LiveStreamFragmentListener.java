package com.vn.ntsc.ui.livestream;

/**
 * Created by nankai on 12/7/2017.
 */

public interface LiveStreamFragmentListener {

    /**
     * when socket is state open
     */
    void open();

    /**
     * when incurred error socket or RTC
     */
    void close();

    /**
     * Join or create new room live stream
     */
    void join();

    /**
     * Connect live stream
     */
    void connect();

    /**
     * leave live stream
     */
    void leave();
}


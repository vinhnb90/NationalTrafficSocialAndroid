package com.vn.ntsc.utils.keyboard;

/**
 * @author Created by Robert on 8/25/2017.
 */
public interface KeyboardToggleListener {
    /**
     * Return keyboard status is opened or closed
     * @param isVisible
     */
    void onToggleSoftKeyboard(boolean isVisible);
}

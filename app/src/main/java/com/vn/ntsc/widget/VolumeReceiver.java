package com.vn.ntsc.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by nankai on 12/29/2017.
 * check volume changed for audio live stream
 */

public class VolumeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            Log.w("Music Stream", "has changed");
        }
    }
}
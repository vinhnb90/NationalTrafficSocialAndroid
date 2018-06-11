package com.vn.ntsc.services.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.utils.SystemUtils;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceIDService";
    private String refreshedToken;

    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Preferences preferences = Preferences.getInstance();
        preferences.setFirebaseRegistrationId(refreshedToken);
        preferences.saveFcmPerAppVersion(SystemUtils.getAppVersion(getApplicationContext()));
    }
}

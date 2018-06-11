package com.vn.ntsc.repository.preferece;

/**
 * Created by nankai on 8/7/2017.
 */

public class GPSADIDPrefernces extends BasePrefers{

    private static GPSADIDPrefernces instance;

    public static final String KEY_GPS_ADID = "key.gps.adid";
    private static final String FILE_PREFERENCES = "meets.adid.preference";

    public static GPSADIDPrefernces getInstance() {
        if (null==instance)
            instance = new GPSADIDPrefernces();
        return instance;
    }

    @Override
    protected String getFileNamePrefers() {
        return FILE_PREFERENCES;
    }

    // ====== ====== add gps ad id ===== ======
    public void saveGPSADID(String adid) {
        getEditor().putString(KEY_GPS_ADID, adid).commit();
    }

    public String getGPSADID() {

        return getPreferences().getString(KEY_GPS_ADID, "");
    }
}

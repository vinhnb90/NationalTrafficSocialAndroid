package com.vn.ntsc.repository.preferece;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by nankai on 8/7/2017.
 */

public class MeetPeopleSetting extends BasePrefers {

    private static MeetPeopleSetting instance;

    public final static int SORT_INTERACTIVE_ALL = 0;
    public final static int SORT_INTERACTIVE_NON = 1;
    public final static int SORT_LOGIN_TIME = 1;
    public final static int SORT_REGISTER_TIME = 2;
    public final static int ALL_SEARCH = -1;
    public final static int FILTER_ALL = 0;
    public final static int FILTER_NEW_REGISTER = 1;
    public final static int FILTER_CALL_WAITING = 2;
    public final static int NEAR_VALUE = 0;
    public final static int CITY_VALUE = 1;
    public final static int COUNTRY_VALUE = 3;
    public final static int REGION_VALUE = 2;
    public final static int WORLD_VALUE = 4;
    //=======================================================//
    //================= variable ============================//
    //=======================================================//
    private static final String KEY_INTERACTIVE = "sort.interactive";
    private final static int MIN_AGE_DEFAULT = 18;
    private final static int MAX_AGE_DEFAULT = 120;
    private final static int DISTANCE_DEFAULT = WORLD_VALUE;
    private final static int FILTER_DEFAULT = FILTER_ALL;
    private final static int SORT_DEFAULT = SORT_LOGIN_TIME;
    private final static boolean IS_NEW_LOGIN_DEFAULT = false;

    private final static int SORT_BODY_DEFAULT = ALL_SEARCH;
    private final static int SORT_AVATAR_DEFAULT = ALL_SEARCH;

    private final static String KEY_MIN_AGE = "key.min.age";
    private final static String KEY_MAX_AGE = "key.max.age";
    private final static String KEY_DISTANCE = "key.distance";
    private final static String KEY_REGION = "key.region";
    private final static String KEY_FILTER = "key.filter";
    private final static String KEY_SORT_TYPE = "key.sort_type";
    private final static String KEY_NEW_LOGIN = "key.is_new_login";
    private final static String KEY_SORT_BODY = "key.sort_body";
    private final static String KEY_SORT_AVATAR = "key.sort_avatar";

    public MeetPeopleSetting() {
        super();
    }

    //=======================================================//
    //================= Constructor ========================//
    //=======================================================//
    public static MeetPeopleSetting getInstance() {
      if (null==instance)
          instance = new MeetPeopleSetting();
        return instance;
    }

    //=======================================================//
    //================= File name ============================//
    //=======================================================//
    @Override
    protected String getFileNamePrefers() {
        return "meets_people_setting";
    }

    //======================================================//
    //=================== Function =========================//
    //======================================================//

    //=================== main age =========================//
    public void saveMinAge(int minAge) {
        getEditor().putInt(KEY_MIN_AGE, minAge).commit();
    }

    public int getMinAge() {
        return getPreferences().getInt(KEY_MIN_AGE, MIN_AGE_DEFAULT);
    }

    //=================== max age =========================//
    public void saveMaxAge(int maxAge) {
        getEditor().putInt(KEY_MAX_AGE, maxAge).commit();
    }

    public int getMaxAge() {
        return getPreferences().getInt(KEY_MAX_AGE, MAX_AGE_DEFAULT);
    }

    //=================== Distance =========================//
    public void saveDistance(int distance) {
        getEditor().putInt(KEY_DISTANCE, distance).commit();
    }

    public int getDistance() {
        return getPreferences().getInt(KEY_DISTANCE, DISTANCE_DEFAULT);
    }

    //=================== Region =========================//
    public void saveRegion(int[] regions) {
        StringBuffer regionsBf = new StringBuffer();
        for (int region : regions) {
            regionsBf.append(region).append(",");
        }
        getEditor().putString(KEY_REGION, regionsBf.toString()).commit();
    }

    public int[] getRegion() {
        String listRegionStr = getPreferences().getString(KEY_REGION, "");
        List<String> listRegion = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(listRegionStr, ",");
        while (st.hasMoreElements()) {
            listRegion.add((String) st.nextElement());
        }
        int[] regions = new int[listRegion.size()];
        int i = 0;
        for (String regionStr : listRegion) {
            regions[i] = Integer.valueOf(regionStr);
            i++;
        }
        return regions;
    }

    //=================== Filter =========================//
    public void saveFilter(int filter) {
        getEditor().putInt(KEY_FILTER, filter).commit();
    }

    public int getFilter() {
        return getPreferences().getInt(KEY_FILTER, FILTER_DEFAULT);
    }

    //=================== Sort Type =========================//
    public void saveSortType(int sortType) {
        getEditor().putInt(KEY_SORT_TYPE, sortType).commit();
    }

    public int getSortType() {
        return getPreferences().getInt(KEY_SORT_TYPE, SORT_DEFAULT);
    }

    public boolean isNewLogin() {
        return getPreferences().getBoolean(KEY_NEW_LOGIN, IS_NEW_LOGIN_DEFAULT);
    }

    //=================== New Login =========================//
    public void setNewLogin(boolean isNewLogin) {
        getEditor().putBoolean(KEY_NEW_LOGIN, isNewLogin).commit();
    }

    //=================== Sort Body =========================//
    public void saveBody(int sortBody) {
        getEditor().putInt(KEY_SORT_BODY, sortBody).commit();
    }

    public int getSortBody() {
        return getPreferences().getInt(KEY_SORT_BODY, SORT_BODY_DEFAULT);
    }

    //=================== Sort Avatar =========================//
    public void saveAvatar(int sortAvatar) {
        getEditor().putInt(KEY_SORT_AVATAR, sortAvatar).commit();
    }

    public int getSortAvatar() {
        return getPreferences().getInt(KEY_SORT_AVATAR, SORT_AVATAR_DEFAULT);
    }

    public void saveInteractive(int sortInteractive) {
        getEditor().putInt(KEY_INTERACTIVE, sortInteractive).commit();
    }

    public int getSortInteractive() {
        return getPreferences().getInt(KEY_INTERACTIVE, SORT_INTERACTIVE_ALL);
    }

    public void clear() {
        getEditor().clear();
    }
}
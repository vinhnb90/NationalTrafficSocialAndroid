package com.vn.ntsc.repository.preferece;

import com.google.gson.Gson;
import com.vn.ntsc.ui.search.SearchSetting;

/**
 * Created by hnc on 21/08/2017.
 */

public class SearchSettingPreference extends BasePrefers {

    private static final String SHAF_SEARCH_SETTING = "PREFERENCE_SEARCH_SETTING";

    private static SearchSettingPreference instance;

    public static synchronized SearchSettingPreference getInstance() {
        if (instance == null) {
            instance = new SearchSettingPreference();
        }
        return instance;
    }

    @Override
    protected String getFileNamePrefers() {
        return "SearchSettingPreference";
    }

    /**
     * @param searchSetting Obj muốn lưu
     *                      Parse object thành stringJson rồi lưu vào sharePref
     */
    public void saveSearchSetting(SearchSetting searchSetting) {
        String settingJson = new Gson().toJson(searchSetting);
        getEditor().putString(SHAF_SEARCH_SETTING, settingJson).commit();
    }

    
    /**
     * Lấy JsonString parse trả về Object
     *
     * @return trả về Object đã lưu
     */
    public SearchSetting getSearchSetting() {
        String settingJson = getPreferences().getString(SHAF_SEARCH_SETTING, "");
        return new Gson().fromJson(settingJson, SearchSetting.class);
    }
}

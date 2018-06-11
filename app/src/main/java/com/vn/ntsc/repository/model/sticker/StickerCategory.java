package com.vn.ntsc.repository.model.sticker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ducng on 12/1/2017.
 */

public class StickerCategory {

    /**
     * cat_id : 574eb73ce4b0ccb75f393e4d
     * jp_des : 2
     * en_des : 2
     * stk_num : 30
     * en_name : 2
     * jp_name : 2
     * cat_url : http://10.64.100.22:81/sticker/category_avatar/b32949d3-e7a0-4159-ab06-86d9079f98f3.png
     * lst_stk : []
     * version : 34
     */

    @SerializedName("cat_id")
    private String catId;
    @SerializedName("jp_des")
    private String jpDes;
    @SerializedName("en_des")
    private String enDes;
    @SerializedName("stk_num")
    private int stkNum;
    @SerializedName("en_name")
    private String enName;
    @SerializedName("jp_name")
    private String jpName;
    @SerializedName("cat_url")
    private String catUrl;
    @SerializedName("version")
    private int version;
    @SerializedName("lst_stk")
    private List<StickerItem> lstStk;

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getJpDes() {
        return jpDes;
    }

    public void setJpDes(String jpDes) {
        this.jpDes = jpDes;
    }

    public String getEnDes() {
        return enDes;
    }

    public void setEnDes(String enDes) {
        this.enDes = enDes;
    }

    public int getStkNum() {
        return stkNum;
    }

    public void setStkNum(int stkNum) {
        this.stkNum = stkNum;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getJpName() {
        return jpName;
    }

    public void setJpName(String jpName) {
        this.jpName = jpName;
    }

    public String getCatUrl() {
        return catUrl;
    }

    public void setCatUrl(String catUrl) {
        this.catUrl = catUrl;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<StickerItem> getLstStk() {
        return lstStk;
    }

    public void setLstStk(List<StickerItem> lstStk) {
        this.lstStk = lstStk;
    }
}

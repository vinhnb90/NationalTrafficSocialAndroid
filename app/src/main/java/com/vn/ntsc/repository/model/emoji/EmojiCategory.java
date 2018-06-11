package com.vn.ntsc.repository.model.emoji;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ducng on 12/13/2017.
 */

public class EmojiCategory {
    /**
     * cat_id : 5a2df1d741afe73a6b2dfdf2
     * en_des : ee
     * vn_des : vv
     * vn_name : v
     * lst_emoji : []
     * emoji_num : 0
     * en_name : e
     * cat_url : http://10.64.100.22:81/emoji/201712/11/351dd11b-8554-472a-8eaa-3829b4553620.gif
     * version : 0
     */

    @SerializedName("cat_id")
    private String catId;
    @SerializedName("en_des")
    private String enDes;
    @SerializedName("vn_des")
    private String vnDes;
    @SerializedName("vn_name")
    private String vnName;
    @SerializedName("emoji_num")
    private int emojiNum;
    @SerializedName("en_name")
    private String enName;
    @SerializedName("cat_url")
    private String catUrl;
    @SerializedName("version")
    private int version;
    @SerializedName("lst_emoji")
    private List<EmojiItem> lstEmoji;

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getEnDes() {
        return enDes;
    }

    public void setEnDes(String enDes) {
        this.enDes = enDes;
    }

    public String getVnDes() {
        return vnDes;
    }

    public void setVnDes(String vnDes) {
        this.vnDes = vnDes;
    }

    public String getVnName() {
        return vnName;
    }

    public void setVnName(String vnName) {
        this.vnName = vnName;
    }

    public int getEmojiNum() {
        return emojiNum;
    }

    public void setEmojiNum(int emojiNum) {
        this.emojiNum = emojiNum;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
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

    public List<EmojiItem> getLstEmoji() {
        return lstEmoji;
    }

    public void setLstEmoji(List<EmojiItem> lstEmoji) {
        this.lstEmoji = lstEmoji;
    }
}

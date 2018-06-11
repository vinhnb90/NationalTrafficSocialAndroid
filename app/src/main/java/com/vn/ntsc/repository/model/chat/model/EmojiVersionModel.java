package com.vn.ntsc.repository.model.chat.model;

/**
 * Created by Doremon on 1/4/2018.
 */

public class EmojiVersionModel {
    String catId;
    int version;


    public EmojiVersionModel() {

    }

    public EmojiVersionModel(String catId, int version) {
        this.catId = catId;
        this.version = version;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

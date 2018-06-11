package com.vn.ntsc.repository.model.chat.model;

/**
 * Created by ducng on 11/17/2017.
 */

public class ChatModel {
    private String userid;
    private String value;
    private String type;
    private String date;
    private boolean isOwn;

    public ChatModel(){

    }

    public ChatModel(String userid, String value, String type, String date, boolean isOwn) {
        this.userid = userid;
        this.value = value;
        this.type = type;
        this.date = date;
        this.isOwn = isOwn;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isOwn() {
        return isOwn;
    }

    public void setOwn(boolean own) {
        isOwn = own;
    }
}

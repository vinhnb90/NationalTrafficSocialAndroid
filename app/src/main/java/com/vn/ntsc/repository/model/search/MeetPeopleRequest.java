package com.vn.ntsc.repository.model.search;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by hnc on 21/08/2017.
 */
public class MeetPeopleRequest extends ServerRequest {

    @SerializedName("upper_age")
    public int upperAge;

    @SerializedName("lower_age")
    public int lowwerAge;

    /**
     * region code in assets/regions.xml
     */
    @SerializedName("region")
    public int[] regionCode;

    /**
     * true : search by login within 24 hour
     * false : search all
     */
    @SerializedName("is_new_login")
    public boolean isNewLogin;

    /**
     * 0 : search by user online
     * 1 : search by user register
     * <p>
     * Đúng thứ tự trên giao diện
     */
    @SerializedName("sort_type")
    public int sortType;

    /**
     * -1 : Tất cả
     * 0 : Nam
     * 1 : Nữ
     */

    @SerializedName("gender")
    public int genderType;

    /**
     * 0: Gầy (Mảnh khảnh)
     * 1: Hơi gầy (Hơi mảnh khảnh)
     * 2: Thấp (Săn chắc)
     * 3: Bình thường
     * 4: Tự tin (Cơ bắp)
     * 5: Quyến rũ (Dáng thể thao)
     * 6: Hơi béo
     * 7: Béo
     * <p>
     * Đúng thứ tự trong file string.xml
     * Đúng thứ tự hiển thị trên giao diện
     */
    @SerializedName("body_type")
    public String bodyType;

    /**
     * 0: Có
     * 1: Không
     * 2: Tất cả
     * <p>
     * Đúng thứ tự trên giao diện
     */
    @SerializedName("is_avatar")
    public int avatar;

    @SerializedName("is_interacted")
    public boolean isInteracted;

    @SerializedName("skip")
    private int skip;

    @SerializedName("take")
    private int take = 20;

    /**
     * 0: all, 1: new, 2: video call
     */
    @SerializedName("filter")
    public int filter = 0;


    /**
     * if Location[lat,long] = [0.0, 0.0] ==> Filter all users
     * if Location[lat,long] !=[0.0, 0.0] ==> Filter and limit all users neighbors
     */
    @SerializedName("lat")
    public double lat = 0.0;

    @SerializedName("long")
    public double lng = 0.0;

    /**
     * NEAR_VALUE = 0;
     * CITY_VALUE = 1;
     * REGION_VALUE = 2;
     * COUNTRY_VALUE = 3;
     * WORLD_VALUE = 4;
     */
    @SerializedName("distance")
    public int distance = 2;


    public MeetPeopleRequest(int lowwerAge, int upperAge, int[] regionCode, boolean isNewLogin,
                             int sortType, int genderType, String bodyType, int avatar, boolean isInteracted, String token, int skip) {
        super("meet_people");
        this.token = token;
        this.upperAge = upperAge;
        this.lowwerAge = lowwerAge;
        this.regionCode = regionCode;
        this.isNewLogin = isNewLogin;
        this.genderType = genderType;
        this.sortType = sortType;
        this.bodyType = bodyType;
        this.avatar = avatar;
        this.isInteracted = isInteracted;
        this.skip = skip;

        // default value
        this.distance = 2;
        this.lat = 0.0;
        this.lng = 0.0;
        this.filter = 0;
        this.take = 20;
    }
}

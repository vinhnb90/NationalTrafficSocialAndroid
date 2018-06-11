package com.vn.ntsc.repository.model.sticker;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

import java.util.List;

/**
 * Created by ThoNh on 9/29/2017.
 */

public class LstCategoryDefaultRequest extends ServerRequest {

    /**
     * token : 7552e82c-aaf0-46a2-8449-54a152f1647b
     * list_cat : [{"cat_id":"574eb345e4b0ccb75f393e2d","version":34}]
     */


    @SerializedName("list_cat")
    private List<ListCatBean> listCat;

    public LstCategoryDefaultRequest(List<ListCatBean> listCat) {
        super("list_updated_sticker_cat");
        this.token = "";
        this.listCat = listCat;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ListCatBean> getListCat() {
        return listCat;
    }

    public void setListCat(List<ListCatBean> listCat) {
        this.listCat = listCat;
    }

    public static class ListCatBean {
        public ListCatBean(String catId, int version) {
            this.catId = catId;
            this.version = version;
        }

        /**
         * cat_id : 574eb345e4b0ccb75f393e2d
         * version : 34
         */



        @SerializedName("cat_id")
        private String catId;
        @SerializedName("version")
        private int version;

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
}

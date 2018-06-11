package com.vn.ntsc.repository.model.emoji;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducng on 12/13/2017.
 */

public class EmojiRequest extends ServerRequest {

    public EmojiRequest() {
        super("list_updated_emoji_cat");
        this.token = "";
        this.listCat = new ArrayList<>();
    }

    /**
     * token : 0dbc8661-9eba-4a27-b592-d481b348d4a6
     * list_cat : [{"cat_id":"5a2ded8c41afe7398d1857bf","version":1},{"cat_id":"5a2df1d741afe73a6b2dfdf2","version":0}]
     */



    @SerializedName("list_cat")
    private List<ListCatBean> listCat;

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
        /**
         * cat_id : 5a2ded8c41afe7398d1857bf
         * version : 1
         */
        @SerializedName("cat_id")
        private String catId;
        @SerializedName("version")
        private int version;

        public ListCatBean(String catId, int version) {
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
}

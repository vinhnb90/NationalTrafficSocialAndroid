package com.vn.ntsc.repository.model.sticker;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThoNh on 9/29/2017.
 */

public class StickerCategoryInfoResponse implements Parcelable {


    /**
     * data : [{"cat_id":"574eb345e4b0ccb75f393e2d","jp_des":"1","en_des":"1","stk_num":30,"en_name":"1","jp_name":"1","cat_url":"http://10.64.100.22:81/sticker/category_avatar/0ebf5800-ea67-4967-a68a-1ff6b8310c0c.png","lst_stk":[{"stk_id":"574eb475e4b0ccb75f393e2e","code":100000,"stk_url":"http://10.64.100.22:81/sticker/sticker/100000.png"},{"stk_id":"574eb47fe4b0ccb75f393e2f","code":100001,"stk_url":"http://10.64.100.22:81/sticker/sticker/100001.png"},{"stk_id":"574eb48ee4b0ccb75f393e30","code":100002,"stk_url":"http://10.64.100.22:81/sticker/sticker/100002.png"},{"stk_id":"574eb49fe4b0ccb75f393e31","code":100003,"stk_url":"http://10.64.100.22:81/sticker/sticker/100003.png"},{"stk_id":"574eb4a9e4b0ccb75f393e32","code":100004,"stk_url":"http://10.64.100.22:81/sticker/sticker/100004.png"},{"stk_id":"574eb595e4b0ccb75f393e33","code":100005,"stk_url":"http://10.64.100.22:81/sticker/sticker/100005.png"},{"stk_id":"574eb59ce4b0ccb75f393e34","code":100006,"stk_url":"http://10.64.100.22:81/sticker/sticker/100006.png"},{"stk_id":"574eb5a8e4b0ccb75f393e35","code":100007,"stk_url":"http://10.64.100.22:81/sticker/sticker/100007.png"},{"stk_id":"574eb5b7e4b0ccb75f393e36","code":100008,"stk_url":"http://10.64.100.22:81/sticker/sticker/100008.png"},{"stk_id":"574eb5d8e4b0ccb75f393e37","code":100009,"stk_url":"http://10.64.100.22:81/sticker/sticker/100009.png"},{"stk_id":"574eb5dfe4b0ccb75f393e38","code":100010,"stk_url":"http://10.64.100.22:81/sticker/sticker/100010.png"},{"stk_id":"574eb5e5e4b0ccb75f393e39","code":100011,"stk_url":"http://10.64.100.22:81/sticker/sticker/100011.png"},{"stk_id":"574eb5efe4b0ccb75f393e3a","code":100012,"stk_url":"http://10.64.100.22:81/sticker/sticker/100012.png"},{"stk_id":"574eb5fce4b0ccb75f393e3b","code":100013,"stk_url":"http://10.64.100.22:81/sticker/sticker/100013.png"},{"stk_id":"574eb606e4b0ccb75f393e3c","code":100014,"stk_url":"http://10.64.100.22:81/sticker/sticker/100014.png"},{"stk_id":"574eb619e4b0ccb75f393e3d","code":100015,"stk_url":"http://10.64.100.22:81/sticker/sticker/100015.png"},{"stk_id":"574eb623e4b0ccb75f393e3e","code":100016,"stk_url":"http://10.64.100.22:81/sticker/sticker/100016.png"},{"stk_id":"574eb62be4b0ccb75f393e3f","code":100017,"stk_url":"http://10.64.100.22:81/sticker/sticker/100017.png"},{"stk_id":"574eb634e4b0ccb75f393e40","code":100018,"stk_url":"http://10.64.100.22:81/sticker/sticker/100018.png"},{"stk_id":"574eb63fe4b0ccb75f393e41","code":100019,"stk_url":"http://10.64.100.22:81/sticker/sticker/100019.png"},{"stk_id":"574eb663e4b0ccb75f393e43","code":100021,"stk_url":"http://10.64.100.22:81/sticker/sticker/100021.png"},{"stk_id":"574eb66de4b0ccb75f393e44","code":100022,"stk_url":"http://10.64.100.22:81/sticker/sticker/100022.png"},{"stk_id":"574eb674e4b0ccb75f393e45","code":100023,"stk_url":"http://10.64.100.22:81/sticker/sticker/100023.png"},{"stk_id":"574eb67ce4b0ccb75f393e46","code":100024,"stk_url":"http://10.64.100.22:81/sticker/sticker/100024.png"},{"stk_id":"574eb684e4b0ccb75f393e47","code":100025,"stk_url":"http://10.64.100.22:81/sticker/sticker/100025.png"},{"stk_id":"574eb68ee4b0ccb75f393e48","code":100026,"stk_url":"http://10.64.100.22:81/sticker/sticker/100026.png"},{"stk_id":"574eb694e4b0ccb75f393e49","code":100027,"stk_url":"http://10.64.100.22:81/sticker/sticker/100027.png"},{"stk_id":"574eb6a4e4b0ccb75f393e4a","code":100028,"stk_url":"http://10.64.100.22:81/sticker/sticker/100028.png"},{"stk_id":"574eb6aee4b0ccb75f393e4b","code":100029,"stk_url":"http://10.64.100.22:81/sticker/sticker/100029.png"},{"stk_id":"574eb6b8e4b0ccb75f393e4c","code":100030,"stk_url":"http://10.64.100.22:81/sticker/sticker/100030.png"}]},{"cat_id":"574eb73ce4b0ccb75f393e4d","jp_des":"2","en_des":"2","stk_num":30,"en_name":"2","jp_name":"2","cat_url":"http://10.64.100.22:81/sticker/category_avatar/b32949d3-e7a0-4159-ab06-86d9079f98f3.png","lst_stk":[{"stk_id":"574eb775e4b0ccb75f393e4e","code":100031,"stk_url":"http://10.64.100.22:81/sticker/sticker/100031.png"},{"stk_id":"574eb795e4b0ccb75f393e4f","code":100032,"stk_url":"http://10.64.100.22:81/sticker/sticker/100032.png"},{"stk_id":"574eb79be4b0ccb75f393e50","code":100033,"stk_url":"http://10.64.100.22:81/sticker/sticker/100033.png"},{"stk_id":"574eb7a4e4b0ccb75f393e51","code":100034,"stk_url":"http://10.64.100.22:81/sticker/sticker/100034.png"},{"stk_id":"574eb7ace4b0ccb75f393e52","code":100035,"stk_url":"http://10.64.100.22:81/sticker/sticker/100035.png"},{"stk_id":"574eb7b5e4b0ccb75f393e53","code":100036,"stk_url":"http://10.64.100.22:81/sticker/sticker/100036.png"},{"stk_id":"574eb7bbe4b0ccb75f393e54","code":100037,"stk_url":"http://10.64.100.22:81/sticker/sticker/100037.png"},{"stk_id":"574eb7c1e4b0ccb75f393e55","code":100038,"stk_url":"http://10.64.100.22:81/sticker/sticker/100038.png"},{"stk_id":"574eb7cae4b0ccb75f393e56","code":100039,"stk_url":"http://10.64.100.22:81/sticker/sticker/100039.png"},{"stk_id":"574eb7d2e4b0ccb75f393e57","code":100040,"stk_url":"http://10.64.100.22:81/sticker/sticker/100040.png"},{"stk_id":"574eb7e0e4b0ccb75f393e58","code":100041,"stk_url":"http://10.64.100.22:81/sticker/sticker/100041.png"},{"stk_id":"574eb805e4b0ccb75f393e59","code":100042,"stk_url":"http://10.64.100.22:81/sticker/sticker/100042.png"},{"stk_id":"574eb810e4b0ccb75f393e5a","code":100043,"stk_url":"http://10.64.100.22:81/sticker/sticker/100043.png"},{"stk_id":"574eb82de4b0ccb75f393e5b","code":100044,"stk_url":"http://10.64.100.22:81/sticker/sticker/100044.png"},{"stk_id":"574eb833e4b0ccb75f393e5c","code":100045,"stk_url":"http://10.64.100.22:81/sticker/sticker/100045.png"},{"stk_id":"574eb83ae4b0ccb75f393e5d","code":100046,"stk_url":"http://10.64.100.22:81/sticker/sticker/100046.png"},{"stk_id":"574eb843e4b0ccb75f393e5e","code":100047,"stk_url":"http://10.64.100.22:81/sticker/sticker/100047.png"},{"stk_id":"574eb84be4b0ccb75f393e5f","code":100048,"stk_url":"http://10.64.100.22:81/sticker/sticker/100048.png"},{"stk_id":"574eb856e4b0ccb75f393e60","code":100049,"stk_url":"http://10.64.100.22:81/sticker/sticker/100049.png"},{"stk_id":"574eb85fe4b0ccb75f393e61","code":100050,"stk_url":"http://10.64.100.22:81/sticker/sticker/100050.png"},{"stk_id":"574eb868e4b0ccb75f393e62","code":100051,"stk_url":"http://10.64.100.22:81/sticker/sticker/100051.png"},{"stk_id":"574eb873e4b0ccb75f393e63","code":100052,"stk_url":"http://10.64.100.22:81/sticker/sticker/100052.png"},{"stk_id":"574eb879e4b0ccb75f393e64","code":100053,"stk_url":"http://10.64.100.22:81/sticker/sticker/100053.png"},{"stk_id":"574eb887e4b0ccb75f393e65","code":100054,"stk_url":"http://10.64.100.22:81/sticker/sticker/100054.png"},{"stk_id":"574eb890e4b0ccb75f393e66","code":100055,"stk_url":"http://10.64.100.22:81/sticker/sticker/100055.png"},{"stk_id":"574eb89ce4b0ccb75f393e67","code":100056,"stk_url":"http://10.64.100.22:81/sticker/sticker/100056.png"},{"stk_id":"574eb8c1e4b0ccb75f393e69","code":100058,"stk_url":"http://10.64.100.22:81/sticker/sticker/100058.png"},{"stk_id":"574eb8cce4b0ccb75f393e6a","code":100059,"stk_url":"http://10.64.100.22:81/sticker/sticker/100059.png"},{"stk_id":"574eb8e5e4b0ccb75f393e6b","code":100060,"stk_url":"http://10.64.100.22:81/sticker/sticker/100060.png"},{"stk_id":"574eb8f9e4b0ccb75f393e6c","code":100061,"stk_url":"http://10.64.100.22:81/sticker/sticker/100061.png"}]},{"cat_id":"59f697f941afe7510c8677a9","jp_des":"Bí Xanh","en_des":"Bí Xanh e","stk_num":0,"en_name":"Bí Xanh e","jp_name":"Bí Xanh","cat_url":"http://10.64.100.22:81/sticker/category_avatar/9802f1e7-6d00-4d83-800f-2eab9d87d050.png","lst_stk":[]}]
     * code : 0
     */

    @SerializedName("code")
    private int code;
    @SerializedName("data")
    public List<StickerCategory> data;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeList(this.data);
    }

    public StickerCategoryInfoResponse() {

    }

    protected StickerCategoryInfoResponse(Parcel in) {
        this.code = in.readInt();
        this.data = new ArrayList<StickerCategory>();
        in.readList(this.data, StickerCategory.class.getClassLoader());
    }

    public static final Creator<StickerCategoryInfoResponse> CREATOR = new Creator<StickerCategoryInfoResponse>() {
        @Override
        public StickerCategoryInfoResponse createFromParcel(Parcel source) {
            return new StickerCategoryInfoResponse(source);
        }

        @Override
        public StickerCategoryInfoResponse[] newArray(int size) {
            return new StickerCategoryInfoResponse[size];
        }
    };
}
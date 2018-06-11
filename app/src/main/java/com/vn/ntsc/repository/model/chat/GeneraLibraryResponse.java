package com.vn.ntsc.repository.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerResponse;

import java.util.List;

/**
 * Created by Doremon on 2/28/2018.
 */

public class GeneraLibraryResponse extends ServerResponse implements Parcelable {

    /**
     * data : {"size_audio":2,"all_file_chat":[{"time_sender":"20180228070031618","thumbnail_url":"https://gvn.xgg.jp:281/image/thumbnail/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","original_url":"https://gvn.xgg.jp:281/image/original_image/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","file_id":"5a96538fde61ef7e67438eed","file_duration":4,"type":"audio","file_url":"https://gvn.xgg.jp:281/file/201802/28/333e1b7d-f22a-420d-84b6-c8336819da32.wav"},{"time_sender":"20180228065754326","thumbnail_url":"https://gvn.xgg.jp:281/image/thumbnail/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","original_url":"https://gvn.xgg.jp:281/image/original_image/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","file_id":"5a9652f2de61ef7e67438eec","file_duration":8,"type":"audio","file_url":"https://gvn.xgg.jp:281/file/201802/28/3ea6e6b9-d25d-429d-af98-83b07fb5d92c.wav"}],"size_all_file":2,"size_image":0,"friend_id":"5a8e3190de61ef7e63858c8e","user_id":"5a8e72a6de61ef7e63858ccb","size_video":0}
     */

    @SerializedName("data")
    private DataBean data;

    protected GeneraLibraryResponse(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GeneraLibraryResponse> CREATOR = new Creator<GeneraLibraryResponse>() {
        @Override
        public GeneraLibraryResponse createFromParcel(Parcel in) {
            return new GeneraLibraryResponse(in);
        }

        @Override
        public GeneraLibraryResponse[] newArray(int size) {
            return new GeneraLibraryResponse[size];
        }
    };

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * size_audio : 2
         * all_file_chat : [{"time_sender":"20180228070031618","thumbnail_url":"https://gvn.xgg.jp:281/image/thumbnail/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","original_url":"https://gvn.xgg.jp:281/image/original_image/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","file_id":"5a96538fde61ef7e67438eed","file_duration":4,"type":"audio","file_url":"https://gvn.xgg.jp:281/file/201802/28/333e1b7d-f22a-420d-84b6-c8336819da32.wav"},{"time_sender":"20180228065754326","thumbnail_url":"https://gvn.xgg.jp:281/image/thumbnail/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","original_url":"https://gvn.xgg.jp:281/image/original_image/201711/15/ba7aa107-064b-4b8e-92f3-d6e597a8f605.gif","file_id":"5a9652f2de61ef7e67438eec","file_duration":8,"type":"audio","file_url":"https://gvn.xgg.jp:281/file/201802/28/3ea6e6b9-d25d-429d-af98-83b07fb5d92c.wav"}]
         * size_all_file : 2
         * size_image : 0
         * friend_id : 5a8e3190de61ef7e63858c8e
         * user_id : 5a8e72a6de61ef7e63858ccb
         * size_video : 0
         */

        @SerializedName("size_audio")
        public int sizeAudio;
        @SerializedName("size_all_file")
        public int sizeAllFile;
        @SerializedName("size_image")
        public int sizeImage;
        @SerializedName("friend_id")
        public String friendId;
        @SerializedName("user_id")
        public String userId;
        @SerializedName("size_video")
        public int sizeVideo;
        @SerializedName("all_file_chat")
        public List<ItemFileChat> allFileChat;


    }


}


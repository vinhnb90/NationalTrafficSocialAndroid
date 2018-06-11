package com.vn.ntsc.repository.model.chat;

import com.google.gson.annotations.SerializedName;
import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by Doremon on 2/28/2018.
 */

public class GeneraLibraryRequest extends ServerRequest {

    /**
     * user_id : 59dedfb041afe748a2037314
     * friend_id : 5a091c5941afe7637829870b
     * type : 0
     * skip : 0
     * take : 100
     * sort : -1
     */
    @SerializedName("user_id")
    private String userId;
    @SerializedName("friend_id")
    private String friendId;
    @SerializedName("type")
    private int type;
    @SerializedName("skip")
    private int skip;
    @SerializedName("take")
    private int take;
    @SerializedName("sort")
    private int sort;

    public GeneraLibraryRequest(String token, String userId, String friendId, int type, int skip, int take, int sort) {
        super("get_file_chat");
        this.token = token;
        this.userId = userId;
        this.friendId = friendId;
        this.type = type;
        this.skip = skip;
        this.take = take;
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "GeneraLibraryRequest{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", friendId='" + friendId + '\'' +
                ", type=" + type +
                ", skip=" + skip +
                ", take=" + take +
                ", sort=" + sort +
                '}';
    }
}

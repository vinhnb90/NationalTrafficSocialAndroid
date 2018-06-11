package com.vn.ntsc.repository.model.poststatus;

import java.util.Map;

import okhttp3.RequestBody;


/**
 * Created by Robert on 2017 Oct 12.
 */
public class PostStatusRequest {

    public RequestBody api;
    public RequestBody token;
    public RequestBody comment;
    public Integer privacy;
    public Map<String, RequestBody> friendAndFileBody;

    public PostStatusRequest() {

    }

    public PostStatusRequest(RequestBody api, RequestBody token, RequestBody comment, Integer privacy, Map<String, RequestBody> friendAndFileBody) {
        this.api = api;
        this.token = token;
        this.comment = comment;
        this.privacy = privacy;
        this.friendAndFileBody = friendAndFileBody;
    }

    public PostStatusRequest setApi(RequestBody api) {
        this.api = api;
        return this;
    }

    public PostStatusRequest setToken(RequestBody token) {
        this.token = token;
        return this;
    }

    public PostStatusRequest setComment(RequestBody comment) {
        this.comment = comment;
        return this;
    }
    public PostStatusRequest setPrivacy(Integer privacy) {
        this.privacy = privacy;
        return this;
    }

    public PostStatusRequest setFriendAndFileBody(Map<String, RequestBody> friendAndFileBody) {
        this.friendAndFileBody = friendAndFileBody;
        return this;
    }

}
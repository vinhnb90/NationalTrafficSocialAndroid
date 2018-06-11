package com.vn.ntsc.repository.model.poststatus.tagfriend;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * The TagFriendFavorites request model send to server
 */
public class TagFriendsFavoriteRequest extends ServerRequest {

    public static String apiListFriendMeFav = "lst_fav";

    private int take;
    private int skip;

    public TagFriendsFavoriteRequest(String api, String token, int take, int skip) {
        super(api);
        this.token = token;
        this.take = take;
        this.skip = skip;
    }
}

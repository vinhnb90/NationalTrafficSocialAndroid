package com.vn.ntsc.repository.model.favorite;

import com.vn.ntsc.core.model.ServerRequest;

/**
 * Created by hnc on 08/08/2017.
 */

public class FriendsFavoriteRequest extends ServerRequest {

    public static String apiListFriendMeFav = "lst_fav";
    public static String apiListFriendFavMe = "lst_fvt";

    private int take;
    private int skip;

    public FriendsFavoriteRequest(String api, String token, int take, int skip) {
        super(api);
        this.token = token;
        this.take = take;
        this.skip = skip;
    }
}

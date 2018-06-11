package com.vn.ntsc.repository.remote;

import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.repository.model.chat.UploadFileResponse;
import com.vn.ntsc.repository.model.editprofile.UploadImageResponse;
import com.vn.ntsc.repository.model.myalbum.AddImageToAlbum.AddImageAlbumResponse;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * use module @Link {@link MediaModule}
 * Only use server {@link com.vn.ntsc.BuildConfig#MEDIA_SERVER }
 * Created by namIT on 8/29/17.
 */
public interface ApiMediaService {
    @POST("/api=upl_img_version_2&token={token}&img_cat=3&sum={sum}")
    Observable<UploadImageResponse> uploadAvatar(@Path("token") String token, @Path("sum") String sum, @Body RequestBody file);

    @POST("/upl_file")
    Observable<UploadFileResponse> uploadFile(@Body RequestBody body);

    @Streaming
    @GET("/api=load_img&img_id={gift_id}&img_kind=4")
    Observable<ResponseBody> onDownloadGiftById(@Path("gift_id") String giftId);

    /**
     * /**
     *
     * @param files         the all file of request
     * @param strTagFriends
     * @return return json data consist (code=0: success, 10101: post status failure; desc)
     */
    @Multipart
    @POST("/add_status")
    Observable<PostStatusResponse> uploadFileMultiPart(@Part("api") RequestBody api,
                                                       @Part("token") RequestBody token,
                                                       @Part("buzz_val") RequestBody comment,
                                                       @Part("privacy") Integer postMode,
                                                       @Part("tag_list") RequestBody strTagFriends,
                                                       @Part("stream_url") String streamUrl,
                                                       @PartMap Map<String, RequestBody> files);

    /**
     * Download Audio, Video
     *
     * @param url link audio,video
     * @return
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String url);

    @Multipart
    @POST("/share_media")
    Observable<PostStatusResponse> shareMediaPost(@Part("api") RequestBody api,
                                                  @Part("token") RequestBody token,
                                                  @Part("buzz_val") RequestBody buzzValue,
                                                  @Part("privacy") RequestBody privacy,
                                                  @Part("tag_list") RequestBody userIdList,
                                                  @Part("share_id") RequestBody buzzIdToShare);


    @POST("/add_image_album")
    Observable<AddImageAlbumResponse> addImageToAlbum(@Body RequestBody request);

    /**
     * for download emoji, sticker
     */
    @GET
    Observable<ResponseBody> download(@Url String url);

}

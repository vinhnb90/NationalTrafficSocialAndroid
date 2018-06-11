package com.vn.ntsc.repository.remote;

import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.accountsetting.ChangeAccFacebookRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangeAccFacebookResponse;
import com.vn.ntsc.repository.model.accountsetting.ChangeEmailRequest;
import com.vn.ntsc.repository.model.accountsetting.ChangeEmailResponse;
import com.vn.ntsc.repository.model.applicationinfo.GetApplicationInfoRequest;
import com.vn.ntsc.repository.model.applicationinfo.GetApplicationInfoResponse;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagRequest;
import com.vn.ntsc.repository.model.applicationinfo.GetUpdateInfoFlagResponse;
import com.vn.ntsc.repository.model.block.addblock.AddBlockResponse;
import com.vn.ntsc.repository.model.block.addblock.AddBlockUserRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListRequest;
import com.vn.ntsc.repository.model.block.blocklst.BlockListResponse;
import com.vn.ntsc.repository.model.block.rmvblock.UnBlockRequest;
import com.vn.ntsc.repository.model.block.rmvblock.UnblockResponse;
import com.vn.ntsc.repository.model.changepassword.ChangePasswordRequest;
import com.vn.ntsc.repository.model.changepassword.ChangePasswordResponse;
import com.vn.ntsc.repository.model.chat.ChatHistoryRequest;
import com.vn.ntsc.repository.model.chat.ChatHistoryResponse;
import com.vn.ntsc.repository.model.chat.GeneraLibraryRequest;
import com.vn.ntsc.repository.model.chat.GeneraLibraryResponse;
import com.vn.ntsc.repository.model.comment.AddCommentRequest;
import com.vn.ntsc.repository.model.comment.AddCommentResponse;
import com.vn.ntsc.repository.model.comment.AddSubCommentRequest;
import com.vn.ntsc.repository.model.comment.AddSubCommentResponse;
import com.vn.ntsc.repository.model.comment.CommentDetailRequest;
import com.vn.ntsc.repository.model.comment.CommentDetailResponse;
import com.vn.ntsc.repository.model.comment.DeleteCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteCommentResponse;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentRequest;
import com.vn.ntsc.repository.model.comment.DeleteSubCommentResponse;
import com.vn.ntsc.repository.model.comment.ListCommentRequest;
import com.vn.ntsc.repository.model.comment.ListCommentResponse;
import com.vn.ntsc.repository.model.comment.ListSubCommentRequest;
import com.vn.ntsc.repository.model.comment.ListSubCommentResponse;
import com.vn.ntsc.repository.model.conversation.ConversationRequest;
import com.vn.ntsc.repository.model.conversation.ConversationResponse;
import com.vn.ntsc.repository.model.conversation.DelConversationRequest;
import com.vn.ntsc.repository.model.conversation.DelConversationResponse;
import com.vn.ntsc.repository.model.conversation.MarkReadAllRequest;
import com.vn.ntsc.repository.model.conversation.MarkReadAllResponse;
import com.vn.ntsc.repository.model.editprofile.EditProfileRequest;
import com.vn.ntsc.repository.model.editprofile.EditProfileResponse;
import com.vn.ntsc.repository.model.emoji.EmojiReponse;
import com.vn.ntsc.repository.model.emoji.EmojiRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.AddFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.FriendsFavoriteResponse;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteRequest;
import com.vn.ntsc.repository.model.favorite.RemoveFavoriteResponse;
import com.vn.ntsc.repository.model.forgotpassword.ForgotPasswordRequest;
import com.vn.ntsc.repository.model.gift.GiftRequest;
import com.vn.ntsc.repository.model.gift.GiftResponse;
import com.vn.ntsc.repository.model.installcount.InstallCountRequest;
import com.vn.ntsc.repository.model.installcount.InstallCountResponse;
import com.vn.ntsc.repository.model.listpublicimage.ListPublicImageRequest;
import com.vn.ntsc.repository.model.listpublicimage.ListPublicImageResponse;
import com.vn.ntsc.repository.model.login.LoginByEmailRequest;
import com.vn.ntsc.repository.model.login.LoginByFacebookRequest;
import com.vn.ntsc.repository.model.login.LoginRequest;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.model.logout.LogoutRequest;
import com.vn.ntsc.repository.model.logout.LogoutResponse;
import com.vn.ntsc.repository.model.myalbum.AddAlbum.AddAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.AddAlbum.AddAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.AddImageToAlbum.AddImageAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.DeleteAlbum.DelAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteAlbum.DelAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.DeleteImageInAlbum.DelAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.LoadAlbum.LoadAlbumResponse;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageRequest;
import com.vn.ntsc.repository.model.myalbum.LoadImageInAlbum.LoadAlbumImageResponse;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumRequest;
import com.vn.ntsc.repository.model.myalbum.UpdateAlbum.UpdateAlbumResponse;
import com.vn.ntsc.repository.model.notification.ClickNotificationRequest;
import com.vn.ntsc.repository.model.notification.ClickNotificationResponse;
import com.vn.ntsc.repository.model.notification.DelNotificationRequest;
import com.vn.ntsc.repository.model.notification.DelNotificationResponse;
import com.vn.ntsc.repository.model.notification.NotificationRequest;
import com.vn.ntsc.repository.model.notification.NotificationResponse;
import com.vn.ntsc.repository.model.notification.OnlineNotificationRequest;
import com.vn.ntsc.repository.model.notification.OnlineNotificationResponse;
import com.vn.ntsc.repository.model.notification.ReadAllNotificationRequest;
import com.vn.ntsc.repository.model.notification.ReadAllNotificationResponse;
import com.vn.ntsc.repository.model.notification.UpdateNotificationRequest;
import com.vn.ntsc.repository.model.notification.UpdateNotificationResponse;
import com.vn.ntsc.repository.model.online.notification.OnlineNotificationNumberRequest;
import com.vn.ntsc.repository.model.online.notification.OnlineNotificationNumberResponse;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.AddOnlineAlertResponse;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertRequest;
import com.vn.ntsc.repository.model.onlinealert.GetOnlineAlertResponse;
import com.vn.ntsc.repository.model.point.GetPointRequest;
import com.vn.ntsc.repository.model.point.GetPointResponse;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteRequest;
import com.vn.ntsc.repository.model.poststatus.tagfriend.TagFriendsFavoriteResponse;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingRequest;
import com.vn.ntsc.repository.model.poststatus.uploadsetting.UploadSettingResponse;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileRequest;
import com.vn.ntsc.repository.model.profile.EvaluateUserProfileResponse;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.report.ReportResponse;
import com.vn.ntsc.repository.model.search.MeetPeopleRequest;
import com.vn.ntsc.repository.model.search.MeetPeopleResponse;
import com.vn.ntsc.repository.model.search.byname.SearchByNameRequest;
import com.vn.ntsc.repository.model.share.AddNumberShareRequest;
import com.vn.ntsc.repository.model.share.AddNumberShareResponse;
import com.vn.ntsc.repository.model.signup.SignUpByFacebookRequest;
import com.vn.ntsc.repository.model.signup.SignUpRequest;
import com.vn.ntsc.repository.model.signup.SignUpResponse;
import com.vn.ntsc.repository.model.sticker.LstCategoryDefaultRequest;
import com.vn.ntsc.repository.model.sticker.StickerCategoryInfoResponse;
import com.vn.ntsc.repository.model.timeline.BuzzDetailRequest;
import com.vn.ntsc.repository.model.timeline.BuzzDetailResponse;
import com.vn.ntsc.repository.model.timeline.BuzzListRequest;
import com.vn.ntsc.repository.model.timeline.BuzzListResponse;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.IncreaseNumberViewVideoRequest;
import com.vn.ntsc.repository.model.timeline.IncreaseNumberViewVideoResponse;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzResponse;
import com.vn.ntsc.repository.model.token.CheckTokenRequest;
import com.vn.ntsc.repository.model.token.CheckTokenResponse;
import com.vn.ntsc.repository.model.user.BannedWordRequest;
import com.vn.ntsc.repository.model.user.BannedWordResponse;
import com.vn.ntsc.repository.model.user.GetUserInfoResponse;
import com.vn.ntsc.repository.model.user.GetUserStatusRequest;
import com.vn.ntsc.repository.model.user.GetUserStatusResponse;
import com.vn.ntsc.repository.model.user.SetCareUserInfoRequest;
import com.vn.ntsc.repository.model.user.SetCareUserInfoResponse;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.videoaudio.VideoAudioResponse;
import com.vn.ntsc.repository.publicfile.PublicFileRequest;
import com.vn.ntsc.repository.publicfile.PublicFileResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * use module @Link {@link com.vn.ntsc.di.modules.NetModule}
 * Only use server {@link com.vn.ntsc.BuildConfig#SERVER_URL }
 * Created by nankai on 8/3/2017.
 */

public interface ApiService {

    @POST("/login")
    Observable<LoginResponse> login(@Body LoginRequest request);

    @POST("/login_email")
    Observable<LoginResponse> loginEmail(@Body LoginByEmailRequest request);

    @POST("/login_facebook")
    Observable<LoginResponse> loginFacebook(@Body LoginByFacebookRequest request);

    @POST("/get_inf_for_application")
    Observable<GetApplicationInfoResponse> getInfoForApplication(@Body GetApplicationInfoRequest request);

    @POST("/install_application")
    Observable<InstallCountResponse> installApplication(@Body InstallCountRequest request);

    @POST("/get_update_info_flag")
    Observable<GetUpdateInfoFlagResponse> updateInfoFlagRequest(@Body GetUpdateInfoFlagRequest request);

    @POST("/get_user_status_by_email")
    Observable<GetUserStatusResponse> getUserStatus(@Body GetUserStatusRequest request);

    @POST("/set_crea_info")
    Observable<SetCareUserInfoResponse> setCeraUserInfo(@Body SetCareUserInfoRequest request);

    @POST("/get_buzz")
    Observable<BuzzListResponse> getBuzzList(@Body BuzzListRequest request);

    @POST("/get_buzz_detail")
    Observable<BuzzDetailResponse> getBuzzListDetail(@Body BuzzDetailRequest request);

    @POST("/like_buzz")
    Observable<LikeBuzzResponse> onLike(@Body LikeBuzzRequest request);

    @POST("/lst_fav")
    Observable<FriendsFavoriteResponse> getListMeFavorites(@Body FriendsFavoriteRequest request);

    @POST("/lst_fvt")
    Observable<FriendsFavoriteResponse> getListFavoriteMe(@Body FriendsFavoriteRequest request);

    @POST("/rmv_fav")
    Observable<RemoveFavoriteResponse> removeFavorite(@Body RemoveFavoriteRequest request);

    @POST("/add_fav")
    Observable<AddFavoriteResponse> onAddFavorite(@Body AddFavoriteRequest request);

    @POST("/del_buzz")
    Observable<DeleteBuzzResponse> onDeleteBuzz(@Body DeleteBuzzRequest request);

    @POST("/add_number_of_share")
    Observable<AddNumberShareResponse> onAddNumberShareBuzz(@Body AddNumberShareRequest request);

    @POST("/add_cmt_version_2")
    Observable<AddCommentResponse> onCommentBuzz(@Body AddCommentRequest request);

    @POST("/del_cmt")
    Observable<DeleteCommentResponse> deleteComment(@Body DeleteCommentRequest request);

    @POST("/lst_cmt")
    Observable<ListCommentResponse> getBuzzListComment(@Body ListCommentRequest request);

    @POST("/comment_detail")
    Observable<CommentDetailResponse> getBuzzListComment(@Body CommentDetailRequest request);

    @POST("/list_sub_comment")
    Observable<ListSubCommentResponse> getSubListComment(@Body ListSubCommentRequest request);

    @POST("/add_sub_comment")
    Observable<AddSubCommentResponse> addSubComment(@Body AddSubCommentRequest request);

    @POST("/delete_sub_comment")
    Observable<DeleteSubCommentResponse> deleteSubComment(@Body DeleteSubCommentRequest request);

    @POST("/sign_up")
    Observable<SignUpResponse> signUp(@Body SignUpRequest signupRequest);

    @POST("/meet_people")
    Observable<MeetPeopleResponse> getResultSearchSetting(@Body MeetPeopleRequest request);

    @POST("/upd_noti_token")
    Observable<UpdateNotificationResponse> getUpdateNotification(@Body UpdateNotificationRequest request);

    /**
     * @param forgotPasswordRequest input
     * @return return only code(0: success, 10: email not exist)
     */
    @POST("/forgot_password")
    Observable<ServerResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);

    @POST("/change_forgot_password")
    Observable<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    @POST("/update_profile")
    Observable<EditProfileResponse> updateProfile(@Body EditProfileRequest editProfileRequest);

    /**
     * @param request input
     * @return list<MeetPeopleBean>
     */
    @POST("/search_by_name")
    Observable<MeetPeopleResponse> searchByName(@Body SearchByNameRequest request);

    @POST("/list_conversation")
    Observable<ConversationResponse> getConversations(@Body ConversationRequest request);

    @POST("/del_conversations")
    Observable<DelConversationResponse> delConversation(@Body DelConversationRequest request);

    @POST("/mark_reads")
    Observable<MarkReadAllResponse> markAllReadConversations(@Body MarkReadAllRequest readAllRequest);

    @POST("/lst_pbimg")
    Observable<ListPublicImageResponse> getListPublicImage(@Body ListPublicImageRequest listPublicImageRequest);

    @POST("/rate_user_voice")
    Observable<EvaluateUserProfileResponse> getEvaluateUserProfile(@Body EvaluateUserProfileRequest evaluateUserProfileRequest);

    @POST("/lst_noti")
    Observable<NotificationResponse> getNotifications(@Body NotificationRequest request);

    @POST("/click_noti_notification")
    Observable<ClickNotificationResponse> markReadNotification(@Body ClickNotificationRequest request);

    @POST("/total_noti_seen")
    Observable<ReadAllNotificationResponse> markReadAllNotification(@Body ReadAllNotificationRequest request);

    @POST("/delete_notification")
    Observable<DelNotificationResponse> delNotification(@Body DelNotificationRequest request);

    @POST("/logout")
    Observable<LogoutResponse> logout(@Body LogoutRequest request);

    @POST("/list_online_alert")
    Observable<OnlineNotificationResponse> getOnlineNotifications(@Body OnlineNotificationRequest request);

    @POST("/get_user_inf")
    Observable<GetUserInfoResponse> getUserInfo(@Body UserInfoRequest request);

    @POST("/rpt")
    Observable<ReportResponse> reportUser(@Body ReportRequest request);

    @POST("/add_blk")
    Observable<AddBlockResponse> addBlockUser(@Body AddBlockUserRequest request);

    @POST("/sign_up_facebook")
    Observable<LoginResponse> signUpFacebook(@Body SignUpByFacebookRequest request);

    @POST("/lst_fav")
    Observable<TagFriendsFavoriteResponse> getListMeFavorites(@Body TagFriendsFavoriteRequest request);

    @POST("/get_onl_alt")
    Observable<GetOnlineAlertResponse> getOnlineAlert(@Body GetOnlineAlertRequest request);

    @POST("/add_onl_alt")
    Observable<AddOnlineAlertResponse> addOnlineAlert(@Body AddOnlineAlertRequest request);

    @POST("/lst_blk")
    Observable<BlockListResponse> getLstBlock(@Body BlockListRequest request);

    @POST("/rmv_blk")
    Observable<UnblockResponse> unblock(@Body UnBlockRequest request);

    @POST("/get_chat_history")
    Observable<ChatHistoryResponse> getChatHistory(@Body ChatHistoryRequest request);

    //{"api":"list_sticker_url","token":"cbffe311-9de0-4bc6-976d-66425e05b0c2"}
    @POST("/list_updated_sticker_cat")
    Observable<StickerCategoryInfoResponse> getCatgorySticker(@Body LstCategoryDefaultRequest request);

    @POST("/list_updated_emoji_cat")
    Observable<EmojiReponse> getCatgoryEmoji(@Body EmojiRequest emojiRequest);

    @POST("/check_token")
    Observable<CheckTokenResponse> checkToken(@Body CheckTokenRequest request);

    @POST("/change_email")
    Observable<ChangeEmailResponse> changeEmail(@Body ChangeEmailRequest request);

    @POST("/chg_pwd")
    Observable<com.vn.ntsc.repository.model.accountsetting.ChangePasswordResponse>

    changePassword(@Body com.vn.ntsc.repository.model.accountsetting.ChangePasswordRequest request);

    @POST("/get_upload_setting")
    Observable<UploadSettingResponse> getUploadSetting(@Body UploadSettingRequest uploadSettingRequest);

    @POST("/get_all_gift")
    Observable<GiftResponse> onDownLoadGif(@Body GiftRequest request);

    @POST("/load_album")
    Observable<LoadAlbumResponse> getMyAlbum(@Body LoadAlbumRequest request);

    @POST("/update_album")
    Observable<UpdateAlbumResponse> updateMyAlbum(@Body UpdateAlbumRequest request);

    @POST("/load_album_image")
    Observable<LoadAlbumImageResponse> getImageAlbum(@Body LoadAlbumImageRequest request);

    @POST("/add_album")
    Observable<AddAlbumResponse> addAlbum(@Body AddAlbumRequest request);

    @POST("/add_image_album")
    Observable<AddImageAlbumResponse> addImageToAlbum(@Body RequestBody request);

    @POST("/del_album")
    Observable<DelAlbumResponse> deleteAlbum(@Body DelAlbumRequest request);

    @POST("/del_image_album")
    Observable<DelAlbumImageResponse> deleteImagesInAlbum(@Body DelAlbumImageRequest request);

    @POST("/lst_pbvideo")
    Observable<VideoAudioResponse> getVideoAudio(@Body PublicFileRequest request);

    @POST("/send_gift")
    Observable<GetPointResponse> getPoint(@Body GetPointRequest request);

    @POST("/list_public_file")
    Observable<PublicFileResponse> getPublicFiles(@Body PublicFileRequest request);

    @POST("/create_account_from_fbid")
    Observable<ChangeAccFacebookResponse> changeAccFacebook(@Body ChangeAccFacebookRequest request);

    @POST("/get_file_chat")
    Observable<GeneraLibraryResponse> getAllFile(@Body GeneraLibraryRequest request);

    @POST("/add_number_of_view")
    Observable<IncreaseNumberViewVideoResponse> increaseViewVideo(@Body IncreaseNumberViewVideoRequest request);

    @POST("/list_user_online_alert")
    Observable<OnlineNotificationNumberResponse> getNumberOnlineNotification(@Body OnlineNotificationNumberRequest request);

    @POST("/get_banned_words")
    Observable<BannedWordResponse> getBannedWords(@Body BannedWordRequest request);
}

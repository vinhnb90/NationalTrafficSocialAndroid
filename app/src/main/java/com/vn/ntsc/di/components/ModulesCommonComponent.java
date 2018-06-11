package com.vn.ntsc.di.components;

import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.di.modules.SocketModule;
import com.vn.ntsc.ui.accountsetting.AccountSettingActivity;
import com.vn.ntsc.ui.blocklst.BlockListActivity;
import com.vn.ntsc.ui.changepassword.ChangePasswordActivity;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.chat.generalibrary.GeneraLibraryActivity;
import com.vn.ntsc.ui.comment.subcomment.SubCommentActivity;
import com.vn.ntsc.ui.conversation.ConversationFragment;
import com.vn.ntsc.ui.forgotpassword.ForgotPasswordActivity;
import com.vn.ntsc.ui.friends.favorite.FriendsFavoriteFragment;
import com.vn.ntsc.ui.gift.GiftActivity;
import com.vn.ntsc.ui.login.LoginActivity;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.mediadetail.album.AlbumDetailMediaActivity;
import com.vn.ntsc.ui.notices.notification.NotificationFragment;
import com.vn.ntsc.ui.notices.online.NotificationOnlineActivity;
import com.vn.ntsc.ui.onlinealert.ManageOnlineAlertActivity;
import com.vn.ntsc.ui.posts.PostStatusActivity;
import com.vn.ntsc.ui.profile.detail.ProfileDetailActivity;
import com.vn.ntsc.ui.profile.media.albumDetail.MyAlbumDetailActivity;
import com.vn.ntsc.ui.profile.media.myalbum.MyAlbumActivity;
import com.vn.ntsc.ui.profile.media.timeline.TimelineUserTabActivity;
import com.vn.ntsc.ui.profile.media.videoaudio.VideoAudioActivity;
import com.vn.ntsc.ui.search.result.SearchResultFragment;
import com.vn.ntsc.ui.signup.SignUpActivity;
import com.vn.ntsc.ui.splash.SplashActivity;
import com.vn.ntsc.ui.tagfriends.TagFriendActivity;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.subjects.Subject;

/**
 * Created by nankai on 16/8/7.
 * class contains all popular models used in application
 */

@Singleton
@Component(modules = {
        AppModule.class,
        SocketModule.class,
        NetModule.class
})
public interface ModulesCommonComponent {

    //Token refresh
    Subject<String> onRefreshTokenSubject();

    // inject Activity
    void inject(MainActivity activity);

    void inject(ChatActivity chatActivity);

    void inject(SplashActivity activity);

    void inject(LoginActivity activity);

    void inject(SignUpActivity signUpActivity);

    void inject(ChangePasswordActivity changePasswordActivity);

    void inject(ForgotPasswordActivity forgotPasswordActivity);

    void inject(SubCommentActivity subCommentActivity);

    void inject(PostStatusActivity postStatusActivity);

    void inject(NotificationOnlineActivity activity);

    void inject(ProfileDetailActivity activity);

    void inject(TagFriendActivity tagFriendActivity);

    void inject(ManageOnlineAlertActivity manageOnlineAlertActivity);

    void inject(BlockListActivity activity);

    void inject(AccountSettingActivity activity);

    void inject(GiftActivity activity);

    void inject(MyAlbumActivity activity);

    void inject(MyAlbumDetailActivity activity);

    void inject(VideoAudioActivity activity);

    void inject(TimelineUserTabActivity activity);

    void inject(GeneraLibraryActivity activity);


    //Fragment
    void inject(FriendsFavoriteFragment friendsFavoriteFragment);

    void inject(SearchResultFragment searchResultFragment);

    void inject(ConversationFragment conversationFragment);

    void inject(NotificationFragment notificationFragment);

    void inject(AlbumDetailMediaActivity activity);

}

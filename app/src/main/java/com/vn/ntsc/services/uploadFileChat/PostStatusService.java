package com.vn.ntsc.services.uploadFileChat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.model.mediafile.MediaFileBean;
import com.vn.ntsc.repository.model.poststatus.PostStatusResponse;
import com.vn.ntsc.repository.model.timeline.datas.BuzzBean;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListTagFriendsBean;
import com.vn.ntsc.repository.preferece.NotificationSettingPreference;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.repository.remote.ApiMediaService;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.services.BaseIntentService;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.notify.NotificationUtils;
import com.vn.ntsc.utils.time.TimeUtils;
import com.vn.ntsc.widget.eventbus.RxEventBus;
import com.vn.ntsc.widget.eventbus.SubjectCode;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by nankai on 11/27/2017.
 */

public class PostStatusService extends BaseIntentService {
    public static final String TAG = PostStatusService.class.getSimpleName();

    public static final String INPUT_TAG_FRIENDS = "uploadFile.service.tag.friends";
    public static final String INPUT_FILE = "input.file";
    public static final String INPUT_YOUR_MIND = "input.your.mind";
    public static final String INPUT_PRIVACY = "input.privacy";
    public static final String INPUT_STREAM = "input.streamUrl";
    public static final String INPUT_TOKEN = "uploadFile.token";

    public static final String EXTRA_NOTIFICATION_POST_STATUS = "extra.notification.post.status";

    private NotificationManager notificationMgr;
    private NotificationCompat.Builder notification;
    private BuzzBean templatePostBean = new BuzzBean();

    @Inject
    ApiService apiService;

    @Inject
    ApiMediaService apiMediaService;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    @Inject
    public PostStatusService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Setting timeout for connect/read/write to server when submit post status
        getServiceMediaComponent(
                new MediaModule(BuildConfig.MEDIA_SERVER,
                        280,
                        480,
                        4800
                        , false)).inject(this);

        notificationMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notification = NotificationUtils.createNotification(getApplicationContext(), R.string.upload_status, TAG);
        LogUtils.e(TAG, TAG + " ---------------------> Start");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LogUtils.e(TAG, TAG + " ---------------------> on Handle Intent");
        if (intent != null) {
            ArrayList<ListTagFriendsBean> taggedFriend = intent.getParcelableArrayListExtra(INPUT_TAG_FRIENDS);
            ArrayList<MediaFileBean> mediaFileBeans = intent.getParcelableArrayListExtra(INPUT_FILE);
            String yourMind = intent.getStringExtra(INPUT_YOUR_MIND);
            int privacy = intent.getIntExtra(INPUT_PRIVACY, Constants.PRIVACY_PUBLIC);
            String streamUrl = intent.getStringExtra(INPUT_STREAM);
            String token = intent.getStringExtra(INPUT_TOKEN);


            final String miliseconds = TimeUtils.convertTimeToMilisecond(TimeUtils.getTimeInLocale()) + "";

            templatePostBean.buzzId = "template&" + miliseconds + "&" + UserPreferences.getInstance().getUserId();
            templatePostBean.userName = UserPreferences.getInstance().getUserName();
            templatePostBean.avatar = UserPreferences.getInstance().getAva();
            templatePostBean.privacy = privacy;
            templatePostBean.buzzTime = miliseconds;
            templatePostBean.region = UserPreferences.getInstance().getRegion();
            templatePostBean.buzzValue = yourMind;
            templatePostBean.childNumber = mediaFileBeans.size();
            templatePostBean.tagList = taggedFriend;
            templatePostBean.tagNumber = taggedFriend.size();

            List<ListBuzzChild> templateListBuzzChildren = new ArrayList<>();
            for (MediaFileBean mediaItem : mediaFileBeans) {
                ListBuzzChild item = new ListBuzzChild();
                if (mediaItem.mediaType == MediaFileBean.VIDEO) {
                    item.buzzType = Constants.BUZZ_TYPE_FILE_VIDEO;
                    item.thumbnailUrl = Uri.parse(mediaItem.mediaUri).getPath();
                } else if (mediaItem.mediaType == MediaFileBean.AUDIO) {
                    item.buzzType = Constants.BUZZ_TYPE_FILE_AUDIO;
                    item.thumbnailUrl = mediaItem.mediaUri;
                } else {
                    item.buzzType = Constants.BUZZ_TYPE_FILE_IMAGE;
                    item.thumbnailUrl = mediaItem.mediaUri;
                }
                templateListBuzzChildren.add(item);
            }
            templatePostBean.listChildBuzzes = templateListBuzzChildren;

            LogUtils.e(TAG, "=======================  Post info ===============================");
            LogUtils.e(TAG, "| TaggedFriend                     : " + taggedFriend.size());
            LogUtils.e(TAG, "| MediaFileBeans                   : " + mediaFileBeans.size());
            LogUtils.e(TAG, "| StreamUrl                        : " + streamUrl);
            LogUtils.e(TAG, "| Token                            : " + token);
            LogUtils.e(TAG, "| Privacy                          : " + privacy);
            LogUtils.e(TAG, "| YourMind                         : " + yourMind);
            LogUtils.e(TAG, "=======================  Template ================================");
            LogUtils.e(TAG, "| BuzzId                           : " + templatePostBean.buzzId);
            LogUtils.e(TAG, "| UserName                         : " + templatePostBean.userName);
            LogUtils.e(TAG, "| BuzzTime                         : " + templatePostBean.buzzTime);
            LogUtils.e(TAG, "| Privacy                          : " + templatePostBean.privacy);
            LogUtils.e(TAG, "| BuzzValue                        : " + templatePostBean.buzzValue);
            LogUtils.e(TAG, "| ChildNumber                      : " + templatePostBean.childNumber);
            LogUtils.e(TAG, "| TagNumber                        : " + templatePostBean.tagNumber);
            LogUtils.e(TAG, "=======================  Template ================================");

            notificationMgr.notify(Constants.NOTI_STATUS_ID, notification.setContentText(getString(R.string.upload_status_progress)).build());

            //push event to timeline fragment update ui
            RxEventBus.publish(SubjectCode.SUBJECT_POST_STATUS, templatePostBean);

            Map<String, RequestBody> params = new HashMap<>();

            try {
                for (MediaFileBean mediaItem : mediaFileBeans) {
                    File file = new File(mediaItem.mediaUri);
                    MediaType mediaType;
                    if (mediaItem.mediaType == MediaFileBean.VIDEO)
                        mediaType = MediaType.parse("video/*");
                    else if (mediaItem.mediaType == MediaFileBean.AUDIO)
                        mediaType = MediaType.parse("audio/*");
                    else mediaType = MediaType.parse("image/*");

                    RequestBody fileReqBody = RequestBody.create(mediaType, file);
                    params.put("files\"; filename=\"" + file.getName(), fileReqBody);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Put all friend Id into JSONArray
            JSONArray jFriendId = new JSONArray();
            for (ListTagFriendsBean friend : taggedFriend) {
                jFriendId.put(friend.userId);
            }

            apiMediaService.uploadFileMultiPart(RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), "add_status"),
                    RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), token),
                    RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), yourMind),
                    privacy,
                    RequestBody.create(null, jFriendId.toString()),
                    "streamUrl",
                    params)
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new Observer<PostStatusResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(PostStatusResponse postStatusResponse) {
                            postStatusResponse.tempId = templatePostBean.buzzId;

                            Gson gson = new Gson();
                            LogUtils.e(TAG, BuildConfig.MEDIA_SERVER + "add_status: " + gson.toJson(postStatusResponse));
                            if (postStatusResponse.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                                PostStatusService.this.onSuccess(postStatusResponse);
                            } else {
                                PostStatusService.this.onError(postStatusResponse);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            PostStatusResponse postStatusResponse = new PostStatusResponse();
                            postStatusResponse.tempId = templatePostBean.buzzId;

                            PostStatusService.this.onError(postStatusResponse);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void onError(PostStatusResponse postStatusResponse) {
        //push event to timeline fragment update ui
        RxEventBus.publish(SubjectCode.SUBJECT_POST_STATUS_ERROR, postStatusResponse);

        if (NotificationSettingPreference.getInstance().isSound())
            notification.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + File.separator + R.raw.notice));
        // config vibrate for notification
        NotificationUtils.vibarateNotification(getApplicationContext());
        notification.setContentText(getApplicationContext().getString(R.string.upload_status_error));

        Notification notify = notification.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationMgr.notify(
                Constants.NOTI_STATUS_ID,
                notify
        );
    }

    private void onSuccess(PostStatusResponse postStatusResponse) {
        //push event to timeline fragment update ui
        RxEventBus.publish(SubjectCode.SUBJECT_POST_STATUS_SUCCESS, postStatusResponse);

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(EXTRA_NOTIFICATION_POST_STATUS, postStatusResponse.data.buzzId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // set intent so it does not start a new activity
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), ActivityResultRequestCode.NOTIFICATION_POST_STATUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        if (NotificationSettingPreference.getInstance().isSound())
            notification.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + File.separator + R.raw.notice));
        // config vibrate for notification
        NotificationUtils.vibarateNotification(getApplicationContext());
        notification.setContentText(getApplicationContext().getString(R.string.upload_status_success));

        Notification notify = notification.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationMgr.notify(
                Constants.NOTI_STATUS_ID,
                notify
        );
    }
}

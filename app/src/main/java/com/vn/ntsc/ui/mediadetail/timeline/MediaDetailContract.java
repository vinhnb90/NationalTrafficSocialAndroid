package com.vn.ntsc.ui.mediadetail.timeline;

import android.content.Context;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.callback.PresenterListener;
import com.vn.ntsc.repository.model.report.ReportRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzRequest;
import com.vn.ntsc.repository.model.timeline.DeleteBuzzResponse;
import com.vn.ntsc.repository.model.timeline.IncreaseNumberViewVideoRequest;
import com.vn.ntsc.repository.model.timeline.LikeBuzzRequest;
import com.vn.ntsc.repository.model.timeline.datas.sub.ListBuzzChild;

import java.io.File;
import java.util.List;

/**
 * Created by ThoNh on 9/11/2017.
 */

public interface MediaDetailContract {

    interface View extends CallbackListener {

        void likeMediaSuccess();

        void likeMediaFailure();

        void reportSuccess();

        void reportFailure();

        void saveImageSuccess();

        void saveImageFailure();

        void buzzNotFound();

        void deleteBuzzSuccess(DeleteBuzzResponse response, ListBuzzChild child);

        void onComplete();

        void downloadComplete(File file);

        void downloadError();

        /**
         * share media post error
         *
         * @see Presenter#shareMedia(String, String, List, String)
         */
        void shareMediaFailure();

        /**
         * success to share buzz
         */
        void shareMediaSuccess();

    }

    interface Presenter extends PresenterListener<View> {
        void likeMedia(LikeBuzzRequest request);

        void deleteMedia(DeleteBuzzRequest request, ListBuzzChild child);

        void saveMedia(Context context, ListBuzzChild child);

        void reportImage(ReportRequest request);

        /**
         * share media post
         */
        void shareMedia(String token, String buzzValue, List<String> listUserId, String buzzId);

        void increaseViewNumberOfVideo(IncreaseNumberViewVideoRequest request);
    }
}

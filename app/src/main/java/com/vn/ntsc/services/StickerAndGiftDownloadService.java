package com.vn.ntsc.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.vn.ntsc.app.AppController;
import com.vn.ntsc.repository.model.chat.model.EmojiModel;
import com.vn.ntsc.repository.model.chat.model.EmojiVersionModel;
import com.vn.ntsc.repository.model.chat.sql.DatabaseHelper;
import com.vn.ntsc.repository.model.emoji.EmojiReponse;
import com.vn.ntsc.repository.model.emoji.EmojiRequest;
import com.vn.ntsc.repository.model.gift.Gift;
import com.vn.ntsc.repository.model.gift.GiftRequest;
import com.vn.ntsc.repository.model.gift.GiftResponse;
import com.vn.ntsc.repository.model.gift.PairGiftResponseBody;
import com.vn.ntsc.repository.model.sticker.LstCategoryDefaultRequest;
import com.vn.ntsc.repository.model.sticker.StickerCategoryInfoResponse;
import com.vn.ntsc.repository.remote.ApiMediaService;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.chats.ChatUtils;
import com.vn.ntsc.widget.views.edittext.EmojiManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by ThoNh on 9/28/2017.
 * service download emoji, sticker, gif
 */
public class StickerAndGiftDownloadService extends BaseIntentService {
    public static final String TAG = StickerAndGiftDownloadService.class.getSimpleName();
    public static final String LANGUAGE = "jp";
    public static final int SKIP = 0;
    public static final int TAKE = Integer.MAX_VALUE / 2;

    private int emojiVer = 0;
    private int stickerVer = 0;

    @Inject
    ApiService mApiService;

    @Inject
    ApiMediaService mApiMediaService;

    @Override
    public void onCreate() {
        super.onCreate();
        getServiceMediaComponent().inject(this);
        LogUtils.e(TAG, "onCreate");
    }

    // StartService to download category and download sticker
    public static void startService(Context context) {
        Intent intent = new Intent(context, StickerAndGiftDownloadService.class);
        context.startService(intent);
    }

    @Inject
    public StickerAndGiftDownloadService() {
        super("StickerAndGiftDownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Observable
                .merge(getEmoji(), loadCategorySticker(), loadGiftToSave())
                .onErrorReturnItem(Observable.empty())
                .subscribe(new DefaultObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        // when all done -> stop service
                        stopSelf();
                    }
                });
    }

    /**
     * Download category sticker
     * Folder lưu sticky chính là Category_id của các Sticky
     */
    private Observable<StickerCategoryInfoResponse> loadCategorySticker() {
        Log.d(TAG, "loadCategorySticker: ");
        List<EmojiVersionModel> versionModels;
        List<LstCategoryDefaultRequest.ListCatBean> listCatBeans = new ArrayList<>();

        if (DatabaseHelper.getInstance(getApplicationContext()).getAllStickerVer().size() != 0) {
            stickerVer = DatabaseHelper.getInstance(getApplicationContext()).getAllStickerVer().size();
            versionModels = DatabaseHelper.getInstance(getApplicationContext()).getAllStickerVer();

            for (int i = 0; i < versionModels.size(); i++) {
                LogUtils.e(TAG, "Stickerdowload :" + versionModels.get(i).getCatId() + " getVersion :" + versionModels.get(i).getVersion());
                listCatBeans.add(new LstCategoryDefaultRequest.ListCatBean(versionModels.get(i).getCatId(), versionModels.get(i).getVersion()));
            }
        }

        final LstCategoryDefaultRequest request = new LstCategoryDefaultRequest(listCatBeans);
        return mApiService
                .getCatgorySticker(request)
                .doOnNext(new Consumer<StickerCategoryInfoResponse>() {
                    @Override
                    public void accept(StickerCategoryInfoResponse stickerCategoryInfo) throws Exception {
                        LogUtils.d(TAG, "stickerCategoryInfo ");
                        if (stickerCategoryInfo == null) return;

                        if (stickerCategoryInfo.data.size() == 0 && stickerVer != 0) return;

                        for (int i = 0; i < stickerCategoryInfo.data.size(); i++) {
                            for (int j = 0; j < stickerCategoryInfo.data.get(i).getLstStk().size(); j++) {
                                ChatUtils.downloadFromUrl(AppController.getAppContext()
                                        , stickerCategoryInfo.data.get(i).getLstStk().get(j).getStkUrl()
                                        , stickerCategoryInfo.data.get(i).getCatId()
                                        , String.valueOf(stickerCategoryInfo.data.get(i).getLstStk().get(j).getStkId()) + "_" + stickerCategoryInfo.data.get(i).getLstStk().get(j).getCode() + ".png"
                                        , stickerCategoryInfo.data.get(i).getStkNum()
                                        , ChatUtils.STICKER);
                            }

                            EmojiVersionModel versionModel = new EmojiVersionModel();
                            if (emojiVer != 0) {
                                LogUtils.e(TAG, "delete sticke");
                                DatabaseHelper.getInstance(getApplicationContext()).deleteStickerVerFlllowCatId(stickerCategoryInfo.data.get(i).getCatId());
                            }
                            LogUtils.e(TAG, "insert version");
                            versionModel.setCatId(stickerCategoryInfo.data.get(i).getCatId());
                            versionModel.setVersion(stickerCategoryInfo.data.get(i).getVersion());
                            DatabaseHelper.getInstance(getApplicationContext()).insertStickerVer(versionModel);
                        }
                    }
                });
    }

    /**
     * dowload emoji
     */
    private Observable<String> getEmoji() {
        EmojiRequest emojiRequest = new EmojiRequest();
        return mApiService
                // get json to get list category
                .getCatgoryEmoji(emojiRequest)
                .subscribeOn(Schedulers.io())
                // save into db
                .flatMapIterable(new Function<EmojiReponse, Iterable<String>>() {
                    @Override
                    public Iterable<String> apply(EmojiReponse emojiReponse) throws Exception {
                        Set<String> result = DatabaseHelper.getInstance(getApplicationContext()).insertEmoji(emojiReponse);
                        Log.d(TAG, "update emoji: ");
                        updateEmoji();
                        return result;
                    }
                })
                // check exist -> don't download
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String url) throws Exception {
                        return !TextUtils.isEmpty(url) && !ChatUtils.predicateEmojiFile(getApplicationContext(), url).exists();
                    }
                })
                .flatMap(new Function<String, ObservableSource<Pair<String, ResponseBody>>>() {
                    @Override
                    public ObservableSource<Pair<String, ResponseBody>> apply(final String url) throws Exception {
                        return mApiMediaService.download(url)
                                .map(new Function<ResponseBody, Pair<String, ResponseBody>>() {
                                    @Override
                                    public Pair<String, ResponseBody> apply(ResponseBody responseBody) throws Exception {
                                        return new Pair<>(ChatUtils.predicateEmojiFile(getApplicationContext(), url).getPath(), responseBody);
                                    }
                                })
                                .onErrorResumeNext(Observable.<Pair<String, ResponseBody>>empty());
                    }
                })
                .subscribeOn(Schedulers.io())
                // save emoji file
                .map(new Function<Pair<String, ResponseBody>, String>() {
                    @Override
                    public String apply(Pair<String, ResponseBody> stringResponseBodyPair) throws Exception {
                        saveFile(stringResponseBodyPair.first, stringResponseBodyPair.second, false);
                        return stringResponseBodyPair.first;
                    }
                })
                .observeOn(Schedulers.io())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        // save local emoji
                        DatabaseHelper.getInstance(getApplicationContext())
                                .updateEmojiLocal(getApplicationContext());

                        Log.d(TAG, "update emoji complete: ");
                        updateEmoji();
                    }
                });
    }

    /**
     * update current emoji data from local db
     */
    private void updateEmoji() {
        // get all emoji
        List<EmojiModel> listEmoji = DatabaseHelper.getInstance(getApplicationContext()).getAllEmoji();
        // set emoji manager
        for (EmojiModel model : listEmoji) {
            EmojiManager.getInstance().addEmoji(model.getCode(), model.getUrl());
        }
    }

    /**
     * save file using Okio
     *
     * @param path         file path to save
     * @param responseBody data to write into file
     * @param isOverride   override file
     * @throws IOException if  write file error
     */
    private void saveFile(String path, ResponseBody responseBody, boolean isOverride) throws IOException {
        File file = new File(path);
        if (!isOverride && file.exists() && file.isFile()) {
            return;
        }

        BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeAll(responseBody.source());
        sink.flush();
        sink.close();
    }

    /**
     * Download gift và lưu vaò thư mục
     * Folder lưu gift chính là Category_id của các Gift
     */
    private Observable<PairGiftResponseBody> loadGiftToSave() {
        Log.d(TAG, "loadGiftToSave: ");
        final GiftRequest request = new GiftRequest(LANGUAGE, SKIP, TAKE);
        return mApiService
                .onDownLoadGif(request)
                .subscribeOn(Schedulers.io())
                // check folder exists
                .filter(new Predicate<GiftResponse>() {
                    @Override
                    public boolean test(@NonNull GiftResponse giftResponse) throws Exception {
                        return giftResponse.mDataGift.size() > 0 && !checkGiftExist(giftResponse.mDataGift.get(0));
                    }
                })
                // foreach item gift
                .flatMapIterable(new Function<GiftResponse, Iterable<Gift>>() {
                    @Override
                    public Iterable<Gift> apply(@NonNull GiftResponse giftResponse) throws Exception {
                        return giftResponse.mDataGift;
                    }
                })
                // convert each item to observer download
                .flatMap(new Function<Gift, ObservableSource<PairGiftResponseBody>>() {
                    @Override
                    public ObservableSource<PairGiftResponseBody> apply(@NonNull final Gift gift) throws Exception {
                        return mApiMediaService.onDownloadGiftById(gift.gift_id)
                                .map(new Function<ResponseBody, PairGiftResponseBody>() {
                                    @Override
                                    public PairGiftResponseBody apply(@NonNull ResponseBody responseBody) throws Exception {
                                        return new PairGiftResponseBody(responseBody, gift);
                                    }
                                });
                    }
                })
                .doOnNext(new Consumer<PairGiftResponseBody>() {
                    @Override
                    public void accept(PairGiftResponseBody pairGiftResponseBody) throws Exception {
                        Gift gift = pairGiftResponseBody.getGift();
                        byte[] data = pairGiftResponseBody.getResponseBody().bytes();
                        ChatUtils.saveGiftFile(AppController.getAppContext(), gift.cat_id, gift.gift_id, data);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * Kiểm tra xem liệu có cần download sticky không
     *
     * @param info Item Category Sticky
     * @return Nếu trong storage với đường dẫn là ChatUtils.getFolderSaveSticky(AppController.getAppContext(), folderNameStorage);
     * Có folder name ứng với Id_category và số lượng item trùng nhau thì có nghĩa là đã download rồi, k cần download nữa
     * true : đã có bộ sticky ứng với category này rồi
     * false: chưa tồn tại bộ sticky của category này
     * <p>
     * Lưu ý số -3 : Khi tải các sticky(zip) và giải nén ra, có chứa thêm 3 mục là : thumbnail folder, file index, image description
     * Vì vậy số lượng sticky chứa trong folder = số lương item - 3
     */

    private static boolean checkGiftExist(Gift info) {
        String folderNameStorage = info.cat_id;
        String pathSticky = ChatUtils.getFolderSaveGift(AppController.getAppContext(), folderNameStorage);
        File folderStickyCat = new File(pathSticky);
        if (folderStickyCat.exists()) {
            int numberStickyStorage = folderStickyCat.list().length - 3; // 3 : thumbnail folder , file index, image description
            Log.e(TAG, "\n\nSticky folder exist:" + folderStickyCat);
            Log.e(TAG, "Number gift in folder " + folderNameStorage + " is:" + numberStickyStorage);
            return true;
        }
        return false;
    }
}

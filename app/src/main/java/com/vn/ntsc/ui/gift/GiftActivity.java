package com.vn.ntsc.ui.gift;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.ActivityResultRequestCode;
import com.vn.ntsc.repository.model.gift.Gift;
import com.vn.ntsc.repository.model.gift.GiftRequest;
import com.vn.ntsc.repository.model.gift.GiftResponse;
import com.vn.ntsc.repository.model.user.UserInfoResponse;
import com.vn.ntsc.services.StickerAndGiftDownloadService;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.chats.ChatUtils;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;

import java.io.File;

import butterknife.BindView;

import static com.vn.ntsc.services.StickerAndGiftDownloadService.LANGUAGE;
import static com.vn.ntsc.services.StickerAndGiftDownloadService.SKIP;
import static com.vn.ntsc.services.StickerAndGiftDownloadService.TAKE;
import static com.vn.ntsc.ui.chat.ChatActivity.EXTRA_USR_PROFILE_BEAN;
import static com.vn.ntsc.utils.chats.ChatUtils.GIFT;
import static com.vn.ntsc.utils.chats.ChatUtils.pathStorage;

/**
 * Created by TuanPC on 10/26/2017.
 */
public class GiftActivity extends BaseActivity<GiftPresenter> implements GiftContract.View, GiftAdapter.IOnItemClickListener {

    private static final String TAG = GiftActivity.class.getSimpleName();

    private static final String ELEMENT_GIFT = "gift_element";
    private static final String RESULT_CODE_KEY = "result_code_key";
    public static final String GIFT_ITEM = "gift_item_need_to_send";

    private GiftAdapter mGiftAdapter;

    @BindView(R.id.activity_gift_grd_choose_gift)
    RecyclerView mGiftList;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    @BindView(R.id.activity_gift_refresh)
    SwipeRefreshLayout refresh;

    private UserInfoResponse mUserProfileBean;

    public static void launch(AppCompatActivity activity, UserInfoResponse user, View view, @ActivityResultRequestCode int requestCode) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ELEMENT_GIFT);
        Intent intent = new Intent();
        intent.setClass(activity, GiftActivity.class);
        intent.putExtra(EXTRA_USR_PROFILE_BEAN, user);
        intent.putExtra(RESULT_CODE_KEY, requestCode);
        activity.startActivityForResult(intent, requestCode, options.toBundle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gift;
    }

    @Override
    public void onCreateView(View rootView) {
        getModulesCommonComponent().inject(this);

        getDataBundle();
    }

    @Override
    public void onViewReady() {
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        refresh.setRefreshing(true);
        GiftRequest giftRequest = new GiftRequest(LANGUAGE, SKIP, TAKE);
        getPresenter().getGiftData(giftRequest);
        int numberOfColumns = 3;
        mGiftAdapter = new GiftAdapter( this);
        mGiftAdapter.setEnableLoadMore(true);
        mGiftList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mGiftList.setAdapter(mGiftAdapter);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGiftAdapter.getData().clear();
                GiftRequest giftRequest = new GiftRequest(LANGUAGE, SKIP, TAKE);
                getPresenter().getGiftData(giftRequest);
            }
        });
    }

    private void getDataBundle() {
        Intent intent = getIntent();
        mUserProfileBean = intent.getParcelableExtra(EXTRA_USR_PROFILE_BEAN);
        Log.d(TAG, mUserProfileBean.toString());
        if (mUserProfileBean == null) {
            Toast.makeText(this, R.string.common_error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Check update gift from server
    // if data is change -> delete folder gift and download from server
    @Override
    public void onGetGiftSuccess(GiftResponse response) {

        mGiftAdapter.addData(response.mDataGift);

        File folferGift = new File(ChatUtils.pathStorage + GIFT + "/" + response.mDataGift.get(0).cat_id);
        File listImgGift[] = folferGift.listFiles();
        if (listImgGift == null) {
            StickerAndGiftDownloadService.startService(this);
        } else {
            boolean check = true;
            for (int i = 0; i < listImgGift.length; i++) {
                String nameGift = Utils.nullToEmpty(listImgGift[i]);
                //Check NPE & OutOfIndexBound String
                if (nameGift.contains("/") && nameGift.contains(".") && nameGift.lastIndexOf("/") > nameGift.lastIndexOf(".")) {
                    nameGift = nameGift.substring(nameGift.lastIndexOf("/") + 1, nameGift.lastIndexOf("."));

                    if (!response.mDataGift.get(i).gift_id.equals(nameGift) && check) {
                        File fDir = new File(pathStorage + GIFT);
                        DeleteRecursive(fDir);
                        StickerAndGiftDownloadService.startService(this);
                        check = false;
                    }
                }
            }
        }
    }

    @Override
    public void onFinish() {
        refresh.setRefreshing(false);
    }

    // Delete Folder Gift
    public static void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);
    }

    @Override
    public void onItemClick(final Gift item, int position) {
        Resources res = getResources();
        String contentDialog = String.format(res.getString(R.string.dialog_confirm_send_gift_content), item.gift_name, mUserProfileBean.userName);
        View customDialogView = getLayoutInflater().inflate(R.layout.view_title_dialog_send_gift, null);
        TextView customContentDialog = customDialogView.findViewById(R.id.content_dialog);
        customContentDialog.setText(contentDialog);

        final DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setCustomView(customDialogView).onPositive(
                R.string.common_send_gift_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        gotoChat(item);
                        // server bao k check point nua
//                        getPresenter().checkPoint(item, UserPreferences.getInstance().getToken(), mUserProfileBean.userId);

                        gotoChat(item);
                        dialogInterface.dismiss();
                    }
                })
                .onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void gotoChat(Gift item) {
        //Put Gift item to ChatActivity
        Intent returnIntent = getIntent();
        if (returnIntent != null && returnIntent.getIntExtra(RESULT_CODE_KEY, -1) == ActivityResultRequestCode.REQUEST_START_SEND_GIFT_FROM_CHAT) {
            returnIntent.putExtra(GIFT_ITEM, item);
            returnIntent.putExtra(EXTRA_USR_PROFILE_BEAN, mUserProfileBean);
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            Intent intent = new Intent(GiftActivity.this, ChatActivity.class);
            intent.putExtra(GIFT_ITEM, item);
            intent.putExtra(EXTRA_USR_PROFILE_BEAN, mUserProfileBean);
            startActivity(intent);
        }
    }

    @Override
    public void notEnoughPoint() {
        final DialogMaterial.Builder builder = new DialogMaterial.Builder(context);
        builder.setContent(R.string.common_not_enough_point)
                .setStyle(Style.HEADER_WITH_TITLE)
                .onNegative(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}

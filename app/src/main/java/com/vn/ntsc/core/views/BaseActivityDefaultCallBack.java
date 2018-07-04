package com.vn.ntsc.core.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.model.NetworkError;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.di.components.ActivityMediaComponent;
import com.vn.ntsc.di.components.DaggerActivityMediaComponent;
import com.vn.ntsc.di.components.LiveStreamComponent;
import com.vn.ntsc.di.components.ModulesCommonComponent;
import com.vn.ntsc.di.components.TimelineComponent;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.repository.model.login.AuthenticationBean;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.repository.preferece.GoogleReviewPreference;
import com.vn.ntsc.repository.preferece.Preferences;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.comments.CommentActivity;
import com.vn.ntsc.ui.comments.subcomment.SubCommentActivity;
import com.vn.ntsc.ui.login.LoginActivity;
import com.vn.ntsc.ui.main.MainActivity;
import com.vn.ntsc.ui.profile.my.MyProfileActivity;
import com.vn.ntsc.ui.signup.SignUpActivity;
import com.vn.ntsc.ui.splash.SplashActivity;
import com.vn.ntsc.ui.webview.WebViewActivity;
import com.vn.ntsc.utils.AuthenticationUtil;
import com.vn.ntsc.utils.LogUtils;

import javax.inject.Inject;

/**
 * {@inheritDoc}
 * <p>
 * Created by nankai on 8/3/2017.
 *
 * @param <T> T extends BasePresenter
 *              After extends this class you need to register activity with DI via the getModulesCommonComponent method in initInject.
 *              Want to communicate with the presenter you call the getPresenter () method.
 *              <p>
 *              only one class BaseActivity is entitled to direct extend
 */
abstract class BaseActivityDefaultCallBack<T extends BasePresenter> extends AppCompatActivity implements CallbackListener, IActivityComponentBuilder {

    private static final String TAG = BaseActivityDefaultCallBack.class.getSimpleName();
    @Nullable
    @Inject
    T presenter;

    DialogMaterial.Builder builder;
    //fix duplicate show fragment
    private int serverInvalidState = -1;

    /**
     * @return T extends BasePresenter
     */
    final public T getPresenter() {
        assert presenter != null;
        if (!presenter.isAttachView())
            presenter.attachView(this);
        return presenter;
    }

    @Override
    final public ActivityMediaComponent getMediaComponent() {
        return DaggerActivityMediaComponent.builder()
                .mediaComponent(AppController.getComponent().getMediaComponent())
                .build();
    }

    @Override
    final public ActivityMediaComponent getMediaComponent(MediaModule mediaModule) {
        return DaggerActivityMediaComponent.builder()
                .mediaComponent(AppController.getComponent().getMediaComponent(mediaModule))
                .build();
    }

    @Override
    final public ActivityMediaComponent getMediaComponent(NetModule netModule, MediaModule mediaModule) {
        return DaggerActivityMediaComponent.builder()
                .mediaComponent(AppController.getComponent().getMediaComponent(netModule, mediaModule))
                .build();
    }

    @Override
    final public ModulesCommonComponent getModulesCommonComponent() {
        return AppController.getComponent().getModulesCommonComponent();
    }

    @Override
    final public LiveStreamComponent getLiveStreamComponent() {
        return AppController.getComponent().getLiveStreamComponent();
    }

    @Override
    final public TimelineComponent getTimelineComponent() {
        return AppController.getComponent().getTimelineComponent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null && presenter.isAttachView())
            presenter.detachView();
    }

    final public DialogMaterial.Builder getDialog(int tag) {
        if (builder == null) {
            builder = new DialogMaterial.Builder(BaseActivityDefaultCallBack.this);
        } else {
            if (builder.isShowing())
                builder.dismiss();
            builder = new DialogMaterial.Builder(BaseActivityDefaultCallBack.this);
        }

        if (builder.getTag() != tag)
            builder.setTag(tag);
        return builder;
    }

    @Override
    final public void onServerResponseInvalid(final int code, final ServerResponse response) {

        if (BaseActivityDefaultCallBack.this instanceof SplashActivity)
            return;

        if (code == serverInvalidState && getDialog(code).isShowing()) {
            LogUtils.w("BaseActivityDefaultCallBack", "code : " + code);
            return;
        }

        serverInvalidState = code;
        switch (code) {
            //Server
            case ServerResponse.DefinitionCode.SERVER_UNKNOWN_ERROR:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.common_error)
                        .setContent(R.string.msg_common_server_unknown_error)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_WRONG_DATA_FORMAT:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.common_error)
                        .setContent(R.string.msg_common_server_data_format)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN:
                if (UserPreferences.getInstance().isLogout())
                    onShowDialogLogin(code);
                break;
            case ServerResponse.DefinitionCode.SERVER_NO_CHANGE:
                break;
            case ServerResponse.DefinitionCode.SERVER_OUT_OF_DATE_API:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.need_update_app)
                        .setContent(R.string.application_version_invalid)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String packageName = getPackageName();
                                Uri URI;
                                try {
                                    URI = Uri.parse(WebViewActivity
                                            .getGooglePlayLink(packageName));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    URI = Uri.parse(WebViewActivity
                                            .getGoogleMaketLink(packageName));
                                }
                                startActivity(new Intent(
                                        Intent.ACTION_VIEW, URI));
                                dialog.dismiss();
                            }
                        })
                        .onNegative(R.string.common_later, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_OLD_VERSION:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.need_update_app)
                        .setContent(R.string.application_version_invalid)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String packageName = getPackageName();
                                Uri URI;
                                try {
                                    URI = Uri.parse(WebViewActivity
                                            .getGooglePlayLink(packageName));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    URI = Uri.parse(WebViewActivity
                                            .getGoogleMaketLink(packageName));
                                }
                                startActivity(new Intent(
                                        Intent.ACTION_VIEW, URI));
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_CHANGE_BACKEND_SETTING:

                break;
            case ServerResponse.DefinitionCode.SERVER_LOCKED_FEATURE:
                break;

            case ServerResponse.DefinitionCode.SERVER_LOOKED_USER:

                break;
            case ServerResponse.DefinitionCode.SERVER_INCORRECT_CODE:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.common_error)
                        .setContent(R.string.msg_common_incorrect_code)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_ALREADY_PURCHASE:

                break;
            case ServerResponse.DefinitionCode.SERVER_BLOCKED_USER:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.common_error)
                        .setContent(R.string.user_block)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (BaseActivityDefaultCallBack.this instanceof MyProfileActivity)
                                    BaseActivityDefaultCallBack.this.finish();
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_USER_NOT_EXIST:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.common_error)
                        .setContent(R.string.dialog_user_deactive)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (BaseActivityDefaultCallBack.this instanceof MyProfileActivity
                                        || BaseActivityDefaultCallBack.this instanceof ChatActivity)
                                    BaseActivityDefaultCallBack.this.finish();
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_NOT_IS_APPROVED:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.common_approved)
                        .setContent(R.string.not_approved_buzz)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_ACCESS_DENIED:
                getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle(R.string.common_approved)
                        .setContent(R.string.not_access_denied)
                        .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (BaseActivityDefaultCallBack.this instanceof CommentActivity || BaseActivityDefaultCallBack.this instanceof SubCommentActivity) {
                                    finish();
                                }
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case ServerResponse.DefinitionCode.SERVER_INVALID_TOKEN:
                if (UserPreferences.getInstance().isLogin()) {

                    AuthenticationUtil.onLogout();

                    getDialog(code).setStyle(Style.HEADER_WITH_NOT_HEADER)
                            .setContent(R.string.not_account_has_changed)
                            .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LoginActivity.launchClearTask(BaseActivityDefaultCallBack.this);
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    onShowDialogLogin(code);
                }
                break;
        }
    }

    @Override
    @UiThread
    public void onFailure(final int code, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (BaseActivityDefaultCallBack.this instanceof SplashActivity) {
                    switch (code) {
                        case NetworkError.NO_INTERNET:
                            Toast.makeText(getApplicationContext(), R.string.common_ms_no_internet, Toast.LENGTH_LONG).show();
                            break;
                        case NetworkError.TIME_OUT:
                            Toast.makeText(getApplicationContext(), R.string.common_ms_timeout, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), R.string.msg_common_unknown_error, Toast.LENGTH_LONG).show();
                            break;
                    }

                    Intent intent = new Intent(BaseActivityDefaultCallBack.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    BaseActivityDefaultCallBack.this.startActivity(intent);
                } else {
                    if (getDialog(code).isShowing())
                        return;
                    switch (code) {
                        case NetworkError.NO_INTERNET:
                            getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                                    .setTitle(R.string.common_not_connect_network)
                                    .setContent(R.string.common_ms_no_internet)
                                    .onPositive(R.string.common_settings_wifi, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent networkSetting = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                            startActivity(networkSetting);
                                        }
                                    })
                                    .onNegative(R.string.common_settings_mobile_internet, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            Intent networkSetting = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                            startActivity(networkSetting);

                                        }
                                    })
                                    .onNeutral(R.string.common_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                            break;
                        case NetworkError.TIME_OUT:
                            getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                                    .setTitle(R.string.common_error)
                                    .setContent(R.string.common_ms_timeout)
                                    .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                            break;
                        default:
                            getDialog(code).setStyle(Style.HEADER_WITH_TITLE)
                                    .setTitle(R.string.common_error)
                                    .setContent(R.string.msg_common_unknown_error)
                                    .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                            break;
                    }
                }
            }
        });
    }

    @Override
    final public void onShowDialogLogin(int tag) {
        if (BaseActivityDefaultCallBack.this instanceof SplashActivity)
            return;

        AuthenticationUtil.onLogout();

        //TODO Updated by Robert about 10337#note-19 to change the button text color on the dialog
        getDialog(tag).setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.sign_description)
                .setButtonTextColor(getResources().getColor(R.color.colorPrimary))
                .onPositive(R.string.common_login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.launch(BaseActivityDefaultCallBack.this);
                    }
                })
                .onNegative(R.string.common_sign_up, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SignUpActivity.launch(BaseActivityDefaultCallBack.this);
                    }
                })
                .onNeutral(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    final public void onAutoLogin(LoginResponse loginResponse) {
        if (!(this instanceof SplashActivity)
                && !(this instanceof SignUpActivity)
                && !(this instanceof LoginActivity)) {
            handleReLogin(loginResponse);
        }
    }

    @Override
    final public void onEmailNotFound(LoginResponse loginResponse) {
        if (BaseActivityDefaultCallBack.this instanceof SplashActivity)
            return;

        AuthenticationUtil.onLogout();

        getDialog(loginResponse.code).setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.auto_login_email_not_found)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(BaseActivityDefaultCallBack.this, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        BaseActivityDefaultCallBack.this.finish();
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    final public void onPasswordNotFound(LoginResponse loginResponse) {
        if (BaseActivityDefaultCallBack.this instanceof SplashActivity)
            return;

        AuthenticationUtil.onLogout();

        getDialog(loginResponse.code).setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(R.string.auto_login_password_not_found)
                .onPositive(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(BaseActivityDefaultCallBack.this, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        BaseActivityDefaultCallBack.this.finish();
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    final public void handleReLogin(LoginResponse loginBean) {
        UserPreferences userPreferences = UserPreferences.getInstance();
        // Save info when login success
        AuthenticationBean authenData = loginBean.authenData;
        userPreferences.saveSuccessLoginData(authenData, true);

        Preferences preferences = Preferences.getInstance();
        // Login time
        preferences.saveTimeRelogin(System.currentTimeMillis());
        // Get banned word
//        DataFetcherService.startLoadDirtyWord(this);
        GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
        googleReviewPreference.saveTurnOffVersion(authenData
                .switchBrowserVersion);
        googleReviewPreference.saveEnableGetFreePoint(authenData
                .isEnableGetFreePoint);
        googleReviewPreference.saveIsTurnOffUserInfo(authenData.isTurnOffUserInfo);

        Intent i = new Intent(BaseActivityDefaultCallBack.this, SplashActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}

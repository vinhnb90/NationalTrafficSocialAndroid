package com.vn.ntsc.ui.webview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.R;
import com.vn.ntsc.app.ActionParam;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.TypeView;
import com.vn.ntsc.repository.model.user.UserInfoRequest;
import com.vn.ntsc.repository.model.webview.ParameterWebView;
import com.vn.ntsc.repository.model.webview.WebViewBean;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.Constants;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.widget.toolbar.ToolbarTitleCenter;
import com.vn.ntsc.widget.views.dialog.CustomConfirmDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;


/**
 * Created by Administrator on 04/21/2017.
 */

public class WebViewActivity extends BaseActivity implements WebViewNavigationBar.IOnNavigationButtonClicked,
        MyWebView.Listener {

    private final String ERROR_INVALID_URL = "Invalid URL";

    // Intent key
    private static final String INTENT_PAGE_TYPE = "page_type";
    private static final String INTENT_PAGE_URL = "page_url";
    private static final String INTENT_PAGE_CONTENT = "page_content";
    private static final String INTENT_PAGE_TITLE = "page_title";
    private static final String INTENT_LOGIN_MOCOM_ID = "mocom_id";
    private static final String INTENT_LOGIN_FAMU_ID = "famu_id";


    // Act intent key
    public final String ACT_INTENT = "act";
    public final String TOKEN_INTENT = "%%token%%";
    public static final String SID_INTENT = "%sid%";
    public static final String UID_INTENT = "%uid%";
    private static final String PRODUCT = "http://fit-app.net/";
    private static final String STAGING = "http://202.32.203.168/";
    private static final String LINK = PRODUCT;

    // Link defined
    private final String LINK_LOGIN_OTHER_SYS = "http://api.bbsystem-app.com/";
    private final String LINK_ANDG_HOMEPAGE = "http://and-g.info";
    private final String LINK_NOTICE = LINK + "tokusyou.html?sid=%%token%%";
    private final String LINK_TERM_OF_USE = LINK + "agreement.php?sid=%%token%%";
    private final String LINK_AUTO_VERIFY_AGE = LINK + "meets/dummy_page/age_vertification.html?sid=%%token%%";
    private final String LINK_AUTO_VERIFY_AGE_TEST = LINK + "meets/dummy_page/age_vertification.html";
    private final String LINK_ABOUT_PAYMENT = LINK + "point/explain/?sid=%%token%%";
    private final String LINK_HOW_TO_USE = LINK + "web/howtouse.php?sid=%%token%%";
    private final String LINK_SUPPORT = LINK + "web/help.php?sid=%%token%%";
    private final String LINK_FREE_POINT = LINK + "otameshi_select.php?sid=%%token%%";
    private final String LINK_BUY_POINT = LINK + "web/buypoint.php?sid=%%token%%";
    private final String LINK_POINT = LINK + "web/point.php?sid=%%token%%";
    private final String LINK_INFORMATION = LINK + "information.php?sid=%%token%%";
    private final String LINK_CONTACT = LINK + "contact.php?sid=%%token%%";
    private final String LINK_QA = LINK + "web/form.php?sid=%%token%%";
    private final String LINK_QA_HISTORY = LINK + "web/form.php?sid=%%token%%&mode=history";

    private static final String LINK_GOOGLE_MAKET = "market://details?id=%1$s";
    private static final String LINK_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=%1$s";

    private final String FREE_PAGE_PRIVACY = "http://fit-app.net/Privacy.html";
    private final String FREE_PAGE_TERM_SERVICE = "http://fit-app.net/kiyaku.html";


    // Static page type to server
    // @param int: 0: Term of Service | 1: Privacy Policy | 2: Term of use
    public static final int STATIC_TYPE_TERM_OF_SERVICE = 0;
    public static final int STATIC_TYPE_PRIVACY_POLICY = 1;
    public static final int STATIC_TYPE_TERM_OF_USE = 2;

    // View

    @BindView(R.id.layout_content_web_view_navigation_bar)
    WebViewNavigationBar navigationBar;

    @BindView(R.id.layout_content_web_view_content)
    MyWebView webView;

    @BindView(R.id.layout_content_web_view_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.toolbar)
    ToolbarTitleCenter mToolbar;

    private ProgressDialog progressDialog;
    // Data

    private String pageContent = "";
    private String startUrl = "";
    private static final int REQUEST_FILECHOOSER = 2888;
    private ValueCallback<Uri> mUploadMessage;


    private static final String TAG = WebViewActivity.class.getName();
    private int pageType;
    private String pageUrl = "";
    private String pageTitle = "";


    public static void launch(AppCompatActivity activity, @TypeView.PageTypeWebView int pageType) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(INTENT_PAGE_TYPE, pageType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void launch(AppCompatActivity activity, WebViewBean webViewBean) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(INTENT_PAGE_TYPE, webViewBean.pageType);
        intent.putExtra(INTENT_PAGE_URL, webViewBean.pageUrl);
        intent.putExtra(INTENT_PAGE_CONTENT, webViewBean.pageContent);
        intent.putExtra(INTENT_PAGE_TITLE, webViewBean.pageTitle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void onCreateView(View rootView) {
        navigationBar.setOnNaviButtonClicked(this);
        webView.setListener(this, this);
        webView.setGeolocationEnabled(false);
        webView.setMixedContentAllowed(true);
        webView.setCookiesEnabled(true);
        webView.setThirdPartyCookiesEnabled(true);
        webView.setWebViewClient(new CustomWebViewClient());

        // Get bundle data set from another activity
        Bundle bundle = getIntent().getExtras();
        initDataFromBundle(bundle);
        checkPageType();
    }

    @Override
    public void onViewReady() {
        Log.d(TAG, "onViewReady: ");
        setSupportActionBar(mToolbar);
        mToolbar.setActionbar(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = newBundle(pageType, pageUrl, pageContent, pageTitle);
        outState.putAll(bundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.KEY_GRANT_STORAGE_PERMISSION:
                onGrantStorage(grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    @Override
    public void onBackPressed() {
        if (pageType == TypeView.PageTypeWebView.PAGE_TYPE_AUTO_VERIFY_AGE || pageType == TypeView.PageTypeWebView.PAGE_TYPE_VERIFY_AGE) {
            backToFirstScreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    @Override
    public void onGoBackClick() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @Override
    public void onGoForwardClick() {
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    @Override
    public void onRefreshClick() {
        webView.reload();
    }

    @Override
    public void onHomeClick() {
        UserPreferences preferences = UserPreferences.getInstance();
        if (preferences.getUserId() != null
                && preferences.getUserId().length() > 0
                && preferences.getFinishRegister() == Constants.FINISH_REGISTER_YES) {
            finish();
        }
    }


    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void loadURLwithToken(String url, String token) {
        if (!TextUtils.isEmpty(token)) {
            if (!url.replace(TOKEN_INTENT, token).equals(url)) {
                url = url.replace(TOKEN_INTENT, token);
            } else {
                url = url.replace(SID_INTENT, token);
            }
        }
        loadURL(url);
    }

    private void loadURLwithUID(String url, String uid) {
        if (!TextUtils.isEmpty(uid)) {
            if (!url.replace(UID_INTENT, uid).equals(url)) {
                url = url.replace(UID_INTENT, uid);
            } else {
                url = url.replace(UID_INTENT, uid);
            }
        }
        LogUtils.d("nankai", "load URL with UID ----------> " + url);
        loadURL(url);
    }

    private void loadURL(String url) {
        LogUtils.i(TAG, String.valueOf("URL:" + url));
        webView.loadUrl(url);
    }

    private static Bundle newBundle(int pageType, String url, String content,
                                    String title) {
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_PAGE_URL, url);
        bundle.putString(INTENT_PAGE_CONTENT, content);
        bundle.putString(INTENT_PAGE_TITLE, title);
        bundle.putInt(INTENT_PAGE_TYPE, pageType);
        return bundle;
    }

    public void backToFirstScreen() {
        UserPreferences userPreferences = UserPreferences.getInstance();
        userPreferences.clear();
        super.onBackPressed();
    }

    public static String getGooglePlayLink(String packageName) {
        return String.format(LINK_GOOGLE_PLAY, String.valueOf(packageName));
    }

    public static String getGoogleMaketLink(String packageName) {
        return String.format(LINK_GOOGLE_MAKET, String.valueOf(packageName));
    }

    private void initDataFromBundle(Bundle bundle) {
        pageType = bundle.getInt(INTENT_PAGE_TYPE);
        pageUrl = bundle.getString(INTENT_PAGE_URL);
        if (pageUrl == null) {
            pageUrl = "";
        }
        pageContent = bundle.getString(INTENT_PAGE_CONTENT);
        if (pageContent == null) {
            pageContent = "";
        }
        pageTitle = bundle.getString(INTENT_PAGE_TITLE);
        if (pageTitle == null) {
            pageTitle = "";
        }
    }

    private void onGrantStorage(int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                openChooserMediaFile();
            }
        }
    }

    private void openChooserMediaFile() {
        Toast.makeText(this, "need create Media Picker", Toast.LENGTH_SHORT).show();
        //todo media picker AnhDN
        /*MediaOptions.Builder mediaBuilder = new MediaOptions.Builder();
        MediaOptions options = mediaBuilder.setIsCropped(false).setFixAspectRatio(false).selectPhoto().build();
		MediaPickerActivity.open(getActivity(), REQUEST_FILECHOOSER, options);*/
    }

    private void checkPageType() {
        updateScreenTitle();
        Log.d(TAG, "checkPageType: " + pageUrl);
        Log.d(TAG, "checkPageType: " + pageType);
        if (pageUrl.trim().length() > 0) {
            // Check action before check token
            if (!noNeedToLoadUrl(pageUrl)) {
                checkTokenWithAutoload(pageUrl);
            }
        } else {
            switch (pageType) {
                //hiepuh
                case TypeView.PageTypeWebView.PAGE_TYPE_INFORMATION:
                    checkTokenWithAutoload(LINK_INFORMATION);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_NOTICE:
                    checkTokenWithAutoload(LINK_NOTICE);
                    break;
                //end
                case TypeView.PageTypeWebView.PAGE_TYPE_WEB_VIEW:
                    loadContent(pageContent);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_LOGIN_OTHER_SYS:
                    checkTokenWithAutoload(LINK_LOGIN_OTHER_SYS);
                    break;
                //---------------------
                case TypeView.PageTypeWebView.PAGE_TYPE_TERM_OF_SERVICE:
                    checkTokenWithAutoload(FREE_PAGE_TERM_SERVICE);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_PRIVACY_POLICY:
                    checkTokenWithAutoload(FREE_PAGE_PRIVACY);
                    break;
                //-------------------
                case TypeView.PageTypeWebView.PAGE_TYPE_TERM_OF_USE:
                    checkTokenWithAutoload(LINK_TERM_OF_USE);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_VERIFY_AGE:
                case TypeView.PageTypeWebView.PAGE_TYPE_AUTO_VERIFY_AGE:
                    if ("release".equalsIgnoreCase(BuildConfig.BUILD_TYPE)) {
                        checkTokenWithAutoload(LINK_AUTO_VERIFY_AGE);
                    } else {
                        checkTokenWithAutoload(LINK_AUTO_VERIFY_AGE_TEST);
                    }
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_ANDG_HOMEPAGE:
                    checkTokenWithAutoload(LINK_ANDG_HOMEPAGE);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_ABOUT_PAYMENT:
                    checkTokenWithAutoload(LINK_ABOUT_PAYMENT);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_HOW_TO_USE:
                    checkTokenWithAutoload(LINK_HOW_TO_USE);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_SUPPORT:
                    loadURLwithUID(LINK_SUPPORT, UserPreferences.getInstance().getUserId().isEmpty() ? "null" : UserPreferences.getInstance().getUserId());
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_CONTACT:
                    checkTokenWithAutoload(LINK_CONTACT);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_FREE_POINT:
                    checkTokenWithAutoload(LINK_FREE_POINT);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_BUY_POINT:
                    checkTokenWithAutoload(LINK_BUY_POINT);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_POINT:
                    checkTokenWithAutoload(LINK_POINT);
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_QA:
                    loadURLwithToken(LINK_QA, UserPreferences.getInstance().getToken());
                    break;
                case TypeView.PageTypeWebView.PAGE_TYPE_QA_HISTORY:
                    loadURLwithToken(LINK_QA_HISTORY, UserPreferences.getInstance().getToken());
                    break;
                default:
                    break;
            }
        }
    }

    private void updateScreenTitle() {
        String title = pageTitle;
        int titleId;
        switch (pageType) {
            case TypeView.PageTypeWebView.PAGE_TYPE_TERM_OF_SERVICE:
                titleId = R.string.settings_terms_of_service_terms_of_service;
                break;
            case TypeView.PageTypeWebView.PAGE_TYPE_PRIVACY_POLICY:
                titleId = R.string.settings_terms_of_service_privacy_policy;
                break;
            case TypeView.PageTypeWebView.PAGE_TYPE_VERIFY_AGE:
                titleId = R.string.title_age_verification;
                break;
            //hiepuh
            case TypeView.PageTypeWebView.PAGE_TYPE_INFORMATION:
                titleId = R.string.information;
                break;
            //end
            default:
                titleId = R.string.app_name;
                break;
        }
        // Set title
        if (title.length() > 0) {
            setScreenTitle(title);
        } else {
            setScreenTitle(titleId);
        }
    }

    private void setScreenTitle(String title) {
        mToolbar.setTitleCenter(title);
    }

    private void setScreenTitle(int title) {
        mToolbar.setTitleCenter(title);
    }

    public boolean noNeedToLoadUrl(String url) {
        // Check HTTP request
        if (!url.startsWith("http:") && !url.startsWith("https:")) {
            return canHandleInvalidUrlProtocolByOS();
        }
        // Remove last /
        int urlLength = url.length();
        final String SPLASH = "/";
        if (url.lastIndexOf(SPLASH) == urlLength - 1) {
            url = url.substring(0, urlLength - 1);
            urlLength = url.length();
        }
        // Split URL by ? to get list expression
        final String HOOK = "?";
        int lastParamIndex = url.lastIndexOf(HOOK);
        if (lastParamIndex < 0 || lastParamIndex + 1 > urlLength) {
            lastParamIndex = url.lastIndexOf(SPLASH);
            if (lastParamIndex < 0 || lastParamIndex + 1 > urlLength) {
                return false;
            }
        }
        String listExpressionString = url.substring(lastParamIndex + 1, urlLength);
        if (listExpressionString == null || listExpressionString.length() <= 0) {
            return false;
        }
        // Split listExpression by & to get expression
        final String AMPERSAND = "&";
        String[] listExpression = listExpressionString.split(Pattern
                .quote(AMPERSAND));
        List<ParameterWebView> parameters = new ArrayList<ParameterWebView>();
        for (String expression : listExpression) {
            // Split expression by = to get operand
            final String COMPARE = "=";
            String[] listOperand = expression.split(Pattern.quote(COMPARE));
            // This code have to have the structure is a=b
            if (listOperand.length != 2) {
                LogUtils.e(TAG, ERROR_INVALID_URL);
                return false;
            }
            // Get key and get value
            String param = listOperand[0];
            String value = listOperand[1];
            parameters.add(new ParameterWebView(param, value));
        }
        return checkAction(parameters);
    }

    private boolean canHandleInvalidUrlProtocolByOS() {
        try {
            // Otherwise allow the OS to handle it
            if (!TextUtils.isEmpty(startUrl)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(startUrl));
                startActivity(intent);
            }
            return true;
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean checkAction(List<ParameterWebView> parameters) {
        ParameterWebView actParam = null;
        actParam = getActionParameterWebView(parameters, actParam);
        if (actParam == null) {
            return false;
        }
        parameters.remove(actParam);
        String valueAct = actParam.value;
        if (ActionParam.TOP.equals(valueAct)) {
            handleActionTop();
        } else if (ActionParam.MY_PROFILE.equals(valueAct)) {
            handleActionMyProfile();
        } else if (valueAct.contains(ActionParam.USER_PROFILE)) {
            handleActionUserProfile(valueAct);
        } else if (ActionParam.MY_PAGE.equals(valueAct)) {
            handleActionMyPage();
        } else if (ActionParam.TERMS.equals(valueAct)) {
            handleActionTerms();
        } else if (ActionParam.PRIVACY.equals(valueAct)) {
            handleActionPrivacy();
        } else if (ActionParam.GOOGLE_PLAY_PAGE.equals(valueAct)) {
            Toast.makeText(this, "go to buy point activity", Toast.LENGTH_SHORT).show();
        } else if (ActionParam.CLOSE_APP.equals(valueAct)) {
            closeApplication();
        } else if (ActionParam.LOGIN_MOCOM.equals(valueAct)) {
            if (handleActionLoginMocom(parameters)) return true;
        } else if (ActionParam.LOGIN_FAMU.equals(valueAct)) {
            if (handleActionLoginFamu(parameters)) return true;
        } else if (ActionParam.CALL_SETTING.equals(valueAct)) {
            handleActionCallSetting();
        } else if (ActionParam.FOLLOWER.equals(valueAct)) {
            handleActionFollower();
        } else if (ActionParam.TIMELINE.equals(valueAct)) {
            handleActionTimeline();
        }
        return false;
    }

    private void handleActionTop() {
        // Action for top page. Open top page
        Toast.makeText(this, "go to main activity, open Top", Toast.LENGTH_SHORT).show();
    }

    private void handleActionMyProfile() {
        // Open my profile
        UserPreferences preferences = UserPreferences.getInstance();
        int finishRegStatus = preferences.getFinishRegister();
        if (finishRegStatus == Constants.FINISH_REGISTER_NO) {
//            ProfileRegisterActivity.launch(this);
        } else {
            Toast.makeText(this, "go to main activity, open fragment home", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void handleActionUserProfile(String valueAct) {
        String id = valueAct.replace(ActionParam.USER_PROFILE_ID, "");
        if (!TextUtils.isEmpty(id)) {
            Toast.makeText(this, "go to main activity, open My Profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleActionMyPage() {
        // Open my page
        Toast.makeText(this, "go to main activity, open My Page", Toast.LENGTH_SHORT).show();
    }

    private void handleActionTerms() {
        // Open the term of service
        pageType = TypeView.PageTypeWebView.PAGE_TYPE_TERM_OF_SERVICE;
        pageUrl = "";
        checkPageType();
    }

    private void handleActionPrivacy() {
        // Open privacy
        pageType = TypeView.PageTypeWebView.PAGE_TYPE_PRIVACY_POLICY;
        pageUrl = "";
        checkPageType();
    }

    private boolean handleActionLoginMocom(List<ParameterWebView> parameters) {
        if (parameters.size() < 1) {
            onLoginFail();
        }
        // Check id parameter
        for (ParameterWebView parameter : parameters) {
            String param = parameter.param;
            if (ActionParam.MOCOM_ID.equals(param)) {
                String valueId = parameter.value;
                onLoginMocom(valueId);
                return true;
            }
        }
        return false;
    }

    private void onLoginFail() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void onLoginMocom(String id) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_LOGIN_MOCOM_ID, id);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private boolean handleActionLoginFamu(List<ParameterWebView> parameters) {
        if (parameters.size() < 1) {
            onLoginFail();
        }
        // Check id parameter
        for (ParameterWebView parameter : parameters) {
            String param = parameter.param;
            if (ActionParam.FAMU_ID.equals(param)) {
                String valueId = parameter.value;
                onLoginFamu(valueId);
                return true;
            }
        }
        return false;
    }

    private void onLoginFamu(String id) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_LOGIN_FAMU_ID, id);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void handleActionCallSetting() {
        // Open call setting page
        Toast.makeText(this, "go to main activity, open IncomingSettingFragment", Toast.LENGTH_SHORT).show();
    }

    private void handleActionFollower() {
        Toast.makeText(this, "go to main activity, open ConnectionFragment", Toast.LENGTH_SHORT).show();
    }

    private void handleActionTimeline() {
        Toast.makeText(this, "go to main activity, open TimeLine-BuzzFragment", Toast.LENGTH_SHORT).show();
    }

    private ParameterWebView getActionParameterWebView(List<ParameterWebView> parameters, ParameterWebView actParam) {
        for (ParameterWebView parameter : parameters) {
            String param = parameter.param;
            if (ACT_INTENT.equals(param)) {
                actParam = parameter;
                break;
            }
        }
        return actParam;
    }

    private void closeApplication() {
        CustomConfirmDialog mDialog = new CustomConfirmDialog(this,
                null, getString(R.string.message_end_app), true);
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.common_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);
                    }
                });
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.common_no),
                (DialogInterface.OnClickListener) null);
        mDialog.show();
    }

    private void checkTokenWithAutoload(String url) {
        if (!checkToken(url)) {
            loadURL(url);
        }
    }

    private boolean checkToken(String url) {
        // Check token include before load page
        if (isConstantToken(url)) {
            LogUtils.i(TAG, String.valueOf("Check URL:" + url));
            // Check token parameter to send token
            UserPreferences preferences = UserPreferences.getInstance();
            String token = preferences.getToken();
            UserInfoRequest userInfoRequest = new UserInfoRequest(token, preferences.getUserId());

//            ((WebViewPresenter) getPresenter()).checkToken(userInfoRequest);

            showProgressDialogWaiting();
            pageUrl = url;
            return true;
        }
        return false;
    }

    private boolean isConstantToken(String url) {
        if (url.contains(TOKEN_INTENT)) {
            return true;
        } else if (url.contains(SID_INTENT)) {
            return true;
        } else {
            return false;
        }
    }

    private void showProgressDialogWaiting() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            String waiting = this.getString(R.string.waiting);
            progressDialog.setMessage(waiting);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void loadContent(String content) {
        if (content != null) {
            LogUtils.i(TAG, String.valueOf("Content:" + content));
            webView.loadData(content, "text/html; charset=utf-8", null);
        }
    }

    private void showLoading() {
        if (progressBar == null) {
            return;
        }
        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        if (progressBar == null) {
            return;
        }
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public WebViewNavigationBar getNavigationBar() {
        return navigationBar;
    }

    public class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: ");
            pageUrl = url;
            if (pageUrl.equals("http://app.meets-app.jp/web/buypoint.php?act=googleplay")) {
                url = "http://app.meets-app.jp/web/buypoint.php?act=googleplay&sid=" + SID_INTENT;
            }
            Uri uri = Uri.parse(url);
            if ("httpnew".equals(uri.getScheme()) == true) {
                String[] urls = url.split("://", 0);
                String transition_url = "http://" + urls[1];
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(transition_url)));
                return true;
            }
            if (!noNeedToLoadUrl(url)) {
                if (!checkToken(url)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            navigationBar.notifyDataSetChanged(view.canGoBack(),
                    view.canGoForward());
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            startUrl = url;
            showLoading();
            navigationBar.notifyDataSetChanged(view.canGoBack(),
                    view.canGoForward());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideLoading();
            navigationBar.notifyDataSetChanged(view.canGoBack(),
                    view.canGoForward());
            if (url.startsWith("http:") || url.startsWith("https:")) {
                updateScreenTitle();
            }
        }
    }
}

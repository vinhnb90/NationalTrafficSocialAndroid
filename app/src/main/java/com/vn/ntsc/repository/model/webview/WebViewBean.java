package com.vn.ntsc.repository.model.webview;

import com.vn.ntsc.repository.TypeView;

/**
 * Created by ThoNh on 9/6/2017.
 */

public class WebViewBean {
    public int pageType = 0;
    public String pageUrl = "";
    public String pageContent = "";
    public String pageTitle = "";

    public WebViewBean(@TypeView.PageTypeWebView int pageType, String pageUrl, String pageContent, String pageTitle) {
        this.pageType = pageType;
        this.pageUrl = pageUrl;
        this.pageContent = pageContent;
        this.pageTitle = pageTitle;
    }
}

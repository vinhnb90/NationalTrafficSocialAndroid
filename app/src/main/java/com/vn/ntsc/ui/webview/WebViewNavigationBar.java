package com.vn.ntsc.ui.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vn.ntsc.R;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.Constants;


public class WebViewNavigationBar extends RelativeLayout implements OnClickListener {
    private IOnNavigationButtonClicked navigationButtonListener;

    private ImageView imgBack;
    private ImageView imgForward;
    private ImageView imgHome;
    private ImageView imgRefresh;

    public WebViewNavigationBar(Context context) {
        super(context);
        initView();
    }

    public WebViewNavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public WebViewNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.web_view_navigation_bar, this, false);
        addView(view);

        imgBack = (ImageView) findViewById(R.id.web_view_navigation_bar_back);
        imgBack.setOnClickListener(this);

        imgForward = (ImageView) findViewById(R.id.web_view_navigation_bar_forward);
        imgForward.setOnClickListener(this);

        imgHome = (ImageView) findViewById(R.id.web_view_navigation_bar_home);
        imgHome.setOnClickListener(this);

        imgRefresh = (ImageView) findViewById(R.id.web_view_navigation_bar_refresh);
        imgRefresh.setOnClickListener(this);

        // Default initial view
        notifyDataSetChanged(false, false);
    }

    public void notifyDataSetChanged(boolean isGoBack, boolean isForward) {
        if (isGoBack) {
            imgBack.setImageResource(R.drawable.ic_chevron_left_little_red_24dp);
        } else {
            imgBack.setImageResource(R.drawable.ic_chevron_left_gray_24dp);
        }

        if (isForward) {
            imgForward.setImageResource(R.drawable.ic_chevron_right_little_red_24dp);
        } else {
            imgForward.setImageResource(R.drawable.ic_chevron_right_gray_24dp);
        }

        UserPreferences preferences = UserPreferences.getInstance();
        if (preferences.getUserId() != null
                && preferences.getUserId().length() > 0
                && preferences.getFinishRegister() == Constants.FINISH_REGISTER_YES) {
            imgHome.setImageResource(R.drawable.ic_home_little_red_24dp);
        } else {
            imgHome.setImageResource(R.drawable.ic_home_gray_24dp);
        }
    }

    public interface IOnNavigationButtonClicked {
        public void onGoBackClick();

        public void onGoForwardClick();

        public void onRefreshClick();

        public void onHomeClick();
    }

    public void setOnNaviButtonClicked(IOnNavigationButtonClicked onNaviButtonClicked) {
        this.navigationButtonListener = onNaviButtonClicked;
    }

    @Override
    public void onClick(View v) {
        if (navigationButtonListener == null) {
            return;
        }

        int id = v.getId();
        switch (id) {
            case R.id.web_view_navigation_bar_back:
                navigationButtonListener.onGoBackClick();
                break;
            case R.id.web_view_navigation_bar_forward:
                navigationButtonListener.onGoForwardClick();
                break;
            case R.id.web_view_navigation_bar_refresh:
                navigationButtonListener.onRefreshClick();
                break;
            case R.id.web_view_navigation_bar_home:
                navigationButtonListener.onHomeClick();
                break;
        }
    }
}
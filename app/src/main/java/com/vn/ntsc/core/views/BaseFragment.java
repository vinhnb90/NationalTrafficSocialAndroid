package com.vn.ntsc.core.views;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.widget.eventbus.RxEventBus;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.ButterKnife;

/**
 * {@inheritDoc}
 * <p>
 * Created by nankai on 8/3/2017.
 * <p>
 * <p>
 * When you extends it again you will have to override 3 methods
 * getLayoutId, initInject vs onViewReady
 * {@link BaseFragment#onCreateView(View, ViewGroup, Bundle)} You initialize the views here before proceeding to {@link BaseFragment#setUserVisibleHint()}
 * {@link BaseFragment#setUserVisibleHint()} Operations related to datalogic processing are implemented in this method instead of using it on {@link BaseFragment#onCreateView(View, ViewGroup, Bundle)}
 * {@link BaseFragment#getLayoutId()} ()} return LayoutId of the activity
 */

public abstract class BaseFragment<T extends BasePresenter> extends BaseFragmentDefaultCallBack<T> {

    protected String TAG;

    protected View rootView;
    protected AppCompatActivity activity;
    protected Context context;
    private boolean isErrorView = false;
    private Handler mHandler;

    final public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    /**
     * Để tránh tình trạng có thể nhìn thấy và {@link Fragment # isVisible ()} thay đổi mâu thuẫn tên     
     */
    private boolean isFragmentVisible;

    /**
     * View có khởi hoàn tất.
     * Thuộc tính thay isAdded
     * IsPrepared , isAdded có khả năng onCreateView nhưng không hoàn thành isAdded
     */
    private boolean isPrepared;

    /**
     * First Load
     */
    private boolean isFirstLoad = true;

    /**
     * @return false when @{@link BaseFragment#setUserVisibleHint()} is call
     */
    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
        this.context = context;
    }

    /**
     * confirm dialog
     *
     * @param dialogMessageResId resource id for show content message
     * @param onOK               when click ok
     */
    final protected void showConfirmDialog(int dialogMessageResId, DialogInterface.OnClickListener onOK) {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(context)
                .setStyle(Style.HEADER_WITH_NOT_HEADER)
                .setContent(dialogMessageResId);
        if (onOK != null) {
            builder.onPositive(R.string.common_ok_2, onOK);
        } else {
            builder.onPositive(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.onNegative(R.string.common_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Deprecated
    @Nullable
    @Override
    final public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TAG = BaseFragment.this.getClass().getSimpleName();
        LogUtils.i(TAG, "onCreateView");
        try {
            rootView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, rootView);
            onCreateView(rootView, container, savedInstanceState);
            isErrorView = false;
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
            isErrorView = true;
            rootView = new View(getActivity());
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.i(TAG, "onViewCreated");
        if (!isErrorView) {
            isFirstLoad = true;
            isPrepared = true;
            lazyLoad();
        }
    }

    @Deprecated
    @Override
    final public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        LogUtils.i(TAG, "setMenuVisibility");
        if (!isErrorView)
            if (isMenuVisible()) {
                onVisible();
            } else {
                onInvisible();
                setUserInvisibleHint(visible);
            }
    }

    @Deprecated
    @Override
    final public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.i(TAG, "onHiddenChanged");
        if (!isErrorView)
            if (!hidden) {
                onVisible();
            } else {
                onInvisible();
                setUserInvisibleHint(hidden);
            }
    }

    @Deprecated
    @Override
    final public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isErrorView)
            LogUtils.w(TAG, "setUserVisibleHint can not work because viewRoot has errors ");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isErrorView)
            LogUtils.w(TAG, "onResume can not work because viewRoot has errors ");

        LogUtils.i(TAG, "onResume");
        Glide.get(getContext()).trimMemory(ComponentCallbacks2.TRIM_MEMORY_MODERATE);
//        Runtime.getRuntime().gc();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isErrorView)
            LogUtils.w(TAG, "onPause can not work because viewRoot has errors ");
        LogUtils.i(TAG, "onPause");
        Glide.get(getContext()).trimMemory(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isErrorView)
            LogUtils.w(TAG, "onPause can not work because viewRoot has errors ");
        LogUtils.i(TAG, "onStop");
    }

    final protected void onVisible() {
        isFragmentVisible = true;
        lazyLoad();
    }

    final protected void onInvisible() {
        isFragmentVisible = false;
    }

    final protected void lazyLoad() {
        isFragmentVisible = isFragmentVisible ? isFragmentVisible : isMenuVisible();
        if (isFirstLoad && (isPrepared && isFragmentVisible)) {
            isFirstLoad = false;
            LogUtils.i(TAG, this.getClass().getSimpleName() + " -------> onViewReady");
            setUserVisibleHint();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i(TAG, "onDestroyView");
        RxEventBus.unregister(this);
        isPrepared = false;
        rootView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy");
        if (null != mHandler)
            try {
                Utils.clearHandler(mHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                // i have no idea why AppCompatActivity#getSupportFragmentManager().getFragments return wrong fragments size, so check null before do something
                if (fragment != null) {
                    LogUtils.i(fragment.getClass().getSimpleName(), "resultCode: " + resultCode + "\n" + "requestCode: " + requestCode);
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    /**
     * @return return LayoutId of the fragment
     */
    protected abstract int getLayoutId();

    /**
     * You initialize the views here before proceeding to {@link BaseFragment#setUserVisibleHint()}
     */
    protected abstract void onCreateView(View rootView, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * Operations related to datalogic processing are implemented in this method instead of using it on {@link BaseFragment#onCreateView(View, ViewGroup, Bundle)}
     * This method is used to dump the data when the current fragment is seen by the user
     */
    protected void setUserVisibleHint() {
        //TODO
    }

    protected void setUserInvisibleHint(boolean visible) {
        //TODO
    }
}
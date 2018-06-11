package com.vn.ntsc.core.views;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.BasePresenter;
import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.di.components.LiveStreamComponent;
import com.vn.ntsc.di.components.ModulesCommonComponent;
import com.vn.ntsc.di.components.TimelineComponent;
import com.vn.ntsc.repository.model.login.LoginResponse;
import com.vn.ntsc.utils.LogUtils;

import javax.inject.Inject;

/**
 * {@inheritDoc}
 *
 * Created by nankai on 8/3/2017.
 *
 * @param <T> T extends BasePresenter
 *              After extends this class you need to register activity with DI via the getModulesCommonComponent method in initInject.
 *              Want to communicate with the presenter you call the getPresenter () method.
 *              <p>
 *              only one class BaseActivity is entitled to direct extend
 */
abstract class BaseFragmentDefaultCallBack<T extends BasePresenter> extends Fragment implements CallbackListener, IFragmentComponentBuilder {
    @Nullable
    @Inject
    T presenter;

    /**
     * @return T extends BasePresenter
     */
    final public T getPresenter() {
        assert presenter != null;
        presenter.attachView(this);
        return presenter;
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
    public void onDestroyView() {
        if (presenter != null && presenter.isAttachView())
            presenter.detachView();
        super.onDestroyView();
    }

    @Override
    final public void onServerResponseInvalid(final int code, final ServerResponse response) {
        if (isAdded() && getActivity() != null && getActivity() instanceof BaseActivityDefaultCallBack)
            ((BaseActivityDefaultCallBack) getActivity()).onServerResponseInvalid(code, response);
    }

    /**
     * @param code Returns the common error code for the app
     * @param msg  Returns the common error message for the app
     */
    @Override
    public void onFailure(final int code, final String msg) {
        if (isAdded() && getActivity() != null && getActivity() instanceof BaseActivityDefaultCallBack)
            ((BaseActivityDefaultCallBack) getActivity()).onFailure(code, msg);
    }

    /**
     * @param loginResponse LoginResponse
     */
    @Override
    final public void onAutoLogin(LoginResponse loginResponse) {
        if (isAdded() && getActivity() != null && getActivity() instanceof BaseActivityDefaultCallBack)
            ((BaseActivityDefaultCallBack) getActivity()).onAutoLogin(loginResponse);
    }

    /**
     * @param loginResponse LoginResponse
     */
    @Override
    final public void onEmailNotFound(LoginResponse loginResponse) {
        if (isAdded() && getActivity() != null && getActivity() instanceof BaseActivityDefaultCallBack)
            ((BaseActivityDefaultCallBack) getActivity()).onEmailNotFound(loginResponse);
    }

    /**
     * @param loginResponse LoginResponse
     */
    @Override
    final public void onPasswordNotFound(LoginResponse loginResponse) {
        if (isAdded() && getActivity() != null && getActivity() instanceof BaseActivityDefaultCallBack)
            ((BaseActivityDefaultCallBack) getActivity()).onPasswordNotFound(loginResponse);
    }

    /**
     *
     */
    @Override
    final public void onShowDialogLogin(int tag) {
        LogUtils.i("BaseFragmentDefaultCallBack", "-->onShowDialogLogin is calling.isAdded()=" + isAdded());
        if (isAdded() && getActivity() != null && getActivity() instanceof BaseActivityDefaultCallBack) {
            LogUtils.i("BaseFragmentDefaultCallBack", "---->");
            ((BaseActivityDefaultCallBack) getActivity()).onShowDialogLogin(tag);
        }
    }
}

package com.vn.ntsc.core.callback;

import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.model.login.LoginResponse;

/**
 * Created by nankai on 8/3/2017.
 */

public interface CallbackListener {
    /**
     * Auto login success
     * @param loginResponse LoginResponse
     */
    void onAutoLogin(LoginResponse loginResponse);

    /**
     * Auto login error email not found
     * @param loginResponse LoginResponse
     */
    void onEmailNotFound(LoginResponse loginResponse);

    /**
     * Auto login error password not found
     * @param loginResponse LoginResponse
     */
    void onPasswordNotFound(LoginResponse loginResponse);

    /**
     *
     * @param code Returns the common error code for the app
     *
     * @param msg Returns the common error message for the app
     */
    void onFailure(final int code, final String msg);

    /**
     *
     * @param code Returns the common error invalid for the app
     *
     * @param response Returns the response
     */
    void onServerResponseInvalid(int code, ServerResponse response);

    void onShowDialogLogin(int tag);
}


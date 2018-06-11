package com.vn.ntsc.core.callback;

import com.vn.ntsc.core.model.NetworkError;
import com.vn.ntsc.core.model.ServerResponse;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by nankai on 4/4/2017.
 */

public abstract class SubscriberCallback<T extends ServerResponse> extends DisposableObserver<T> {

    private CallbackListener callbackListener;

    public SubscriberCallback(CallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (this.callbackListener != null) {
            NetworkError error = new NetworkError();
            error.ShowError(callbackListener, e);
        }

        //not call function onCompleted when error network
        onComplete();
    }

    @Override
    final public void onNext(T response) {
        if (response != null) {
            if (response.code == ServerResponse.DefinitionCode.SERVER_SUCCESS) {
                onSuccess(response);
            }
            callbackListener.onServerResponseInvalid(response.code, response);
        } else {
            onError(new NullPointerException());
        }
        onComplete();
    }

    @Override
    final public void onComplete() {
        onCompleted();
    }

    /**
     * @param response Server response returns code equal to 0 and will call this method
     */
    public abstract void onSuccess(T response);

    public abstract void onCompleted();
}
package com.vn.ntsc.core.model;

import com.vn.ntsc.core.callback.CallbackListener;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.SystemUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

/**
 * Created by nankai on 8/23/2017.
 */

public class NetworkError {

    public static final int TIME_OUT = 100;
    public static final int NO_INTERNET = 103;
    public static final int UNKNOWN = 404;

    public void ShowError(CallbackListener callbackListener, Throwable e) {

        //no connection
        if (!SystemUtils.isNetworkConnected()) {
//            if (e instanceof HttpException) {
//                HttpException httpException = ((HttpException) e);
//                LogUtils.e("onError", "-------------- HttpException ---------------- "
//                        + "\n|------------------------------------"
//                        + "\n| MSG : " + e.getMessage()
//                        + "\n| Code: " + httpException.code()
//                        + "\n|------------------------------------");
//            } else if (e instanceof SocketTimeoutException) {
//                SocketTimeoutException socketTimeoutException = ((SocketTimeoutException) e);
//                LogUtils.e("onError", "-------------- SocketTimeoutException ---------------- "
//                        + "\n|------------------------------------"
//                        + "\n| MSG : " + e.getMessage()
//                        + "\n| Code: " + socketTimeoutException.getMessage()
//                        + "\n|------------------------------------");
//            } else if (e instanceof ConnectException) {
//                ConnectException connectException = ((ConnectException) e);
//                LogUtils.e("onError", "-------------- SocketTimeoutException ---------------- "
//                        + "\n|------------------------------------"
//                        + "\n| MSG : " + e.getMessage()
//                        + "\n| Code: " + connectException.getMessage()
//                        + "\n|------------------------------------");
//            } else if (e instanceof IOException) {
//                IOException ioException = ((IOException) e);
//                LogUtils.e("onError", "-------------- IOException ---------------- "
//                        + "\n|------------------------------------"
//                        + "\n| MSG : " + e.getMessage()
//                        + "\n| Code: " + ioException.getMessage()
//                        + "\n|------------------------------------");
//            } else {
//                LogUtils.e("onError", "-------------- HttpException ---------------- "
//                        + "\n|------------------------------------"
//                        + "\n| MSG : " + e.getMessage()
//                        + "\n| Code: " + 404
//                        + "\n|------------------------------------");
//            }
            callbackListener.onFailure(NetworkError.NO_INTERNET, e.getMessage());
        } else {
            //has open network connection
            if (e instanceof HttpException) {

                HttpException httpException = ((HttpException) e);
                LogUtils.e("onError", "-------------- HttpException ---------------- "
                        + "\n|------------------------------------"
                        + "\n| MSG : " + e.getMessage()
                        + "\n| Code: " + httpException.code()
                        + "\n|------------------------------------");
                callbackListener.onFailure(httpException.code(), ((HttpException) e).message());

            } else if (e instanceof SocketTimeoutException) {

                SocketTimeoutException socketTimeoutException = ((SocketTimeoutException) e);
                LogUtils.e("onError", "-------------- SocketTimeoutException ---------------- "
                        + "\n|------------------------------------"
                        + "\n| MSG : " + e.getMessage()
                        + "\n| Code: " + socketTimeoutException.getMessage()
                        + "\n|------------------------------------");
                callbackListener.onFailure(NetworkError.TIME_OUT, e.getMessage());

            } else if (e instanceof ConnectException) {
                ConnectException connectException = ((ConnectException) e);
                LogUtils.e("onError", "-------------- SocketTimeoutException ---------------- "
                        + "\n|------------------------------------"
                        + "\n| MSG : " + e.getMessage()
                        + "\n| Code: " + connectException.getMessage()
                        + "\n|------------------------------------");
                callbackListener.onFailure(NetworkError.TIME_OUT, e.getMessage());
            } else if (e instanceof IOException) {
                IOException ioException = ((IOException) e);
                // use 3/4G will throw this exception cause server only accept ip from gvn
                LogUtils.e("onError", "-------------- IOException ---------------- "
                        + "\n|------------------------------------"
                        + "\n| MSG : " + e.getMessage()
                        + "\n| Code: " + ioException.getMessage()
                        + "\n|------------------------------------");
                callbackListener.onFailure(NetworkError.UNKNOWN, e.getMessage());
            } else {
                LogUtils.e("onError", "-------------- HttpException ---------------- "
                        + "\n|------------------------------------"
                        + "\n| MSG : " + e.getMessage()
                        + "\n| Code: " + 404
                        + "\n|------------------------------------");
                callbackListener.onFailure(NetworkError.UNKNOWN, e.getMessage());
            }
        }
    }
}

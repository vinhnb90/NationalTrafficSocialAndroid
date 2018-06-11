package com.vn.ntsc.widget.views.interceptors;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.vn.ntsc.app.AppController;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.repository.preferece.UserPreferences;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.subjects.Subject;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by nankai on 1/22/2018.
 * Check if expired token is detected, update the new token and retry the request api with the new token
 */

public class RewriteResponseInterceptor implements Interceptor {

    private static final String TAG = "Interceptor";
    private Gson gson;
    private Subject<String> refreshTokenSubject;

    public RewriteResponseInterceptor(Gson gson, Subject<String> refreshTokenSubject) {
        this.gson = gson;
        this.refreshTokenSubject = refreshTokenSubject;
        new CacheControl.Builder()
                .maxAge(60, TimeUnit.SECONDS)
                .maxStale(300, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        return onCheckRetryWhenTokenExpires(chain, originalRequest);
    }

    private Response onCheckRetryWhenTokenExpires(Chain chain, Request originalRequest) throws IOException {

        originalRequest = originalRequest
                .newBuilder()
                .build();

        //Make the first time to get the response
        Response originalResponse = chain.proceed(originalRequest);

        //Check is login
        if (UserPreferences.getInstance().isLogin()) {
            //Convert response to json
            String jsonResponse = Utils.bodyResponseToString(originalResponse.body()) == null ? "" : Utils.bodyResponseToString(originalResponse.body());
            //Convert json to ServerResponse
            ServerResponse serverResponse = gson.fromJson(jsonResponse, ServerResponse.class) == null ? new ServerResponse() : gson.fromJson(jsonResponse, ServerResponse.class);
            //Check response code  = 3 retry new request when invalid token and receipt new_token from server
            if (serverResponse.code == ServerResponse.DefinitionCode.SERVER_EXPIRED_TOKEN
                    && !Utils.isEmptyOrNull(serverResponse.newToken)) {
                LogUtils.w(TAG, "Preparing data to retry when has new token from server=" + serverResponse.newToken);
                return onRetryWhenTokenExpires(chain, originalRequest, originalResponse, serverResponse);
            }
        }

        return originalResponse;
    }

    private Response onRetryWhenTokenExpires(Chain chain, Request originalRequest, Response originalResponse, ServerResponse serverResponse) throws IOException {

        String jsonRequest;
        String jsonResponse;

        jsonRequest = Utils.bodyRequestToString(originalRequest);
        jsonResponse = Utils.bodyResponseToString(originalResponse.body()) == null ? "" : Utils.bodyResponseToString(originalResponse.body());
        LogUtils.w(TAG, "old request : " + jsonRequest);
        LogUtils.w(TAG, "old response: " + jsonResponse);

        //Save token to Preference
        UserPreferences.getInstance().saveToken(serverResponse.newToken);

        //Retry connect socket
        AppController.getAppContext().onConnectSocket();

        //Send a new token to the listener subject
        refreshTokenSubject.onNext(serverResponse.newToken);

        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //Convert Request to json string
            String jsonBodyRequest = Utils.bodyRequestToString(originalRequest);
            //Initialize the new request with a new token
            JSONObject jsonObject = new JSONObject(jsonBodyRequest);
            jsonObject.putOpt("token", serverResponse.newToken);

            RequestBody newBodyRequest = RequestBody.create(JSON, jsonObject.toString());// create new RequestBody

            //New creation request
            originalRequest = chain.request()
                    .newBuilder()
                    .method(originalRequest.method(), newBodyRequest)
                    .build();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Close old respond, 1 ic_progress just 1 proceed
        originalResponse.close();

        //New creation response
        originalResponse = chain.proceed(originalRequest);

        jsonRequest = Utils.bodyRequestToString(originalRequest);
        jsonResponse = Utils.bodyResponseToString(originalResponse.body()) == null ? "" : Utils.bodyResponseToString(originalResponse.body());
        LogUtils.w(TAG, "new request: " + jsonRequest);
        LogUtils.w(TAG, "new response: " + jsonResponse);

        return originalResponse;
    }
}

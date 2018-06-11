package com.vn.ntsc.di.modules;

import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.repository.remote.ApiMediaService;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dev22 on 8/29/17.
 */
@Module
public class MediaModule {

    private long CONNECT_TIMEOUT = BuildConfig.TIMEOUT_CONNECT;
    private long READ_TIMEOUT = BuildConfig.TIMEOUT_READ;
    private long WRITE_TIMEOUT = BuildConfig.TIMEOUT_WRITE;
    private boolean LOGGING_PRIORITY = true;//Default no priority, if false then set to NONE for upload large file

    private String mUrl;

    public MediaModule(String mUrl) {
        this.mUrl = mUrl;
    }

    public MediaModule(String mUrl, long connectTimeout, long readTimeout, long writeTimeout) {
        this.mUrl = mUrl;
        this.CONNECT_TIMEOUT = connectTimeout;
        this.READ_TIMEOUT = readTimeout;
        this.WRITE_TIMEOUT = writeTimeout;

    }

    public MediaModule(String mUrl, long connectTimeout, long readTimeout, long writeTimeout, boolean loggingPriority) {
        this.mUrl = mUrl;
        this.CONNECT_TIMEOUT = connectTimeout;
        this.READ_TIMEOUT = readTimeout;
        this.WRITE_TIMEOUT = writeTimeout;
        this.LOGGING_PRIORITY = loggingPriority;
    }

    @Singleton
    @Provides
    @Named("retrofitForUpload")
    Retrofit provideRetrofitForUpload() {

        //setup cache
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG && LOGGING_PRIORITY) {
            //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        OkHttpClient mOkHttpClient = builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS).build();

        return new Retrofit.Builder()
                .baseUrl(mUrl)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    ApiMediaService provideUploadImageService(@Named("retrofitForUpload") Retrofit retrofit) {
        return retrofit.create(ApiMediaService.class);
    }
}

package com.vn.ntsc.di.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vn.ntsc.BuildConfig;
import com.vn.ntsc.repository.remote.ApiService;
import com.vn.ntsc.widget.views.interceptors.RewriteResponseInterceptor;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by simple on 8/3/17.
 */
@Module
public class NetModule {

    private long CONNECT_TIMEOUT = BuildConfig.TIMEOUT_CONNECT;
    private long READ_TIMEOUT = BuildConfig.TIMEOUT_READ;
    private long WRITE_TIMEOUT = BuildConfig.TIMEOUT_WRITE;
    private boolean LOGGING_PRIORITY = true;//Default no priority, if false then set to NONE for upload large file

    private String mUrl;

    public NetModule(String mUrl) {
        this.mUrl = mUrl;
    }

    public NetModule(String mUrl, boolean loggingPriority) {
        this.mUrl = mUrl;
        this.LOGGING_PRIORITY = loggingPriority;
    }

    public NetModule(String mUrl, long connectTimeout, long readTimeout, long writeTimeout) {
        this.mUrl = mUrl;
        this.CONNECT_TIMEOUT = connectTimeout;
        this.READ_TIMEOUT = readTimeout;
        this.WRITE_TIMEOUT = writeTimeout;

    }

    public NetModule(String mUrl, long connectTimeout, long readTimeout, long writeTimeout, boolean loggingPriority) {
        this.mUrl = mUrl;
        this.CONNECT_TIMEOUT = connectTimeout;
        this.READ_TIMEOUT = readTimeout;
        this.WRITE_TIMEOUT = writeTimeout;
        this.LOGGING_PRIORITY = loggingPriority;
    }

//    @Singleton
//    @Provides
//    File provideFile() {
//        return FileUtils.getDiskCacheDir(AppController.getAppContext(), Constants.CACHE_DIR_NAME);
//    }
//
//    @Singleton
//    @Provides
//    Cache provideCacheFile(File httpCacheDirectory) {
//        return FileUtils.getCache(httpCacheDirectory,Constants.MAX_CACHE_SIZE);
//    }

    @Provides
    Subject<String> provideRefreshTokenSubject() {
        return PublishSubject.create();
    }

    @Singleton
    @Provides
    GsonBuilder provideGsonBuilder() {
        return new GsonBuilder();
    }

    @Singleton
    @Provides
    Gson provideGson(GsonBuilder builder) {
        return builder.create();
    }

    @Singleton
    @Provides
    RewriteResponseInterceptor provideResponseCacheInterceptor(final Subject<String> refreshTokenSubject, final Gson gson) {
        return new RewriteResponseInterceptor(gson, refreshTokenSubject);
    }

    @Singleton
    @Provides
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor,   /*Cache cache,*/ RewriteResponseInterceptor networkInterceptor) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG && LOGGING_PRIORITY) {
            //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            builder.addInterceptor(loggingInterceptor);
        }

//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                // Create a trust manager that does not validate certificate chains
//                final TrustManager[] trustAllCerts = new TrustManager[]{
//                        new X509TrustManager() {
//                            @SuppressLint("TrustAllX509TrustManager")
//                            @Override
//                            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
//                                    throws CertificateException {
//                                // Do nothing, the client is trusted
//                            }
//
//                            @SuppressLint("TrustAllX509TrustManager")
//                            @Override
//                            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
//                                    throws CertificateException {
//                                // Do nothing, the server is trusted
//                            }
//
//                            @Override
//                            public X509Certificate[] getAcceptedIssuers() {
//                                return new X509Certificate[]{};
//                            }
//                        }
//                };
//                // Install the all-trusting trust manager
//                SSLContext sslContext = SSLContext.getInstance("SSL");
//                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//                // Create an ssl socket factory with our all-trusting manager
//                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
//                builder.hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                });
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (KeyManagementException e) {
//                e.printStackTrace();
//            }
//        }

        //setup cache
        
        return builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(networkInterceptor)
                /*.cache(cache)*/
                .build();
    }

    @Singleton
    @Provides
    RxJava2CallAdapterFactory provideRxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(OkHttpClient mOkHttpClient, RxJava2CallAdapterFactory rxJava2CallAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(mUrl)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .build();
    }

    @Singleton
    @Provides
    ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
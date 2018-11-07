package com.xiongms.libcore.di.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiongms.libcore.BuildConfig;
import com.xiongms.libcore.config.NetConfig;
import com.xiongms.libcore.di.qualifiers.RQBOkHttpClient;
import com.xiongms.libcore.di.qualifiers.RQBRetrofit;
import com.xiongms.libcore.network.converter.gson.RQBGsonConverterFactory;
import com.xiongms.libcore.network.converter.scalar.RQBScalarsConverterFactory;
import com.xiongms.libcore.network.interceptor.CommonParamInterceptor;
import com.xiongms.libcore.network.interceptor.CommonHeaderInterceptor;
import com.xiongms.libcore.network.interceptor.LoggingInterceptor;
import com.xiongms.libcore.network.interceptor.RetryIntercept;


import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author xiongms
 * @time 2018-08-16 10:07
 */
@Module
public class NetModule {

    private static final String TAG = NetModule.class.getSimpleName();

    @Singleton
    @Provides
    Gson providesGson() {
        return new GsonBuilder()
                .create();
    }

    @Singleton
    @Provides
    LoggingInterceptor providesHttpLoggingInterceptor() {
        LoggingInterceptor logging = new LoggingInterceptor();
        return logging;
    }


    @Provides
    @Singleton
    RetryIntercept provideApiRetryIntercept() {
        return new RetryIntercept(NetConfig.NET_MAX_RETRY_TIMES);
    }

    @Provides
    @Singleton
    CommonHeaderInterceptor provideCommonHeaderInterceptor() {
        return new CommonHeaderInterceptor();
    }

    @Provides
    @Singleton
    CommonParamInterceptor provideApiRequestCommonParamInterceptor() {
        return new CommonParamInterceptor();
    }


    @Singleton
    @Provides
    X509TrustManager providesTrustManagers() {
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        };
        return x509TrustManager;
    }


    @Singleton
    @Provides
    SSLSocketFactory providesRQBSSLSocketFactory(
            X509TrustManager trustManagers) {

        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManagers}, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return sslSocketFactory;
    }

    @Singleton
    @Provides
    @RQBOkHttpClient
    OkHttpClient providesRQBOkHttpClient(CommonHeaderInterceptor commonHeaderInterceptor,
                                         LoggingInterceptor httpLoggingInterceptor,
                                         CommonParamInterceptor commonParamInterceptor,
                                         RetryIntercept retryIntercept,
                                         X509TrustManager x509TrustManager,
                                         SSLSocketFactory sslSocketFactory) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(NetConfig.NET_TIME_OUT_CONNECT, TimeUnit.SECONDS);
        builder.readTimeout(NetConfig.NET_TIME_OUT_READ, TimeUnit.SECONDS);
        builder.writeTimeout(NetConfig.NET_TIME_OUT_WRITE, TimeUnit.SECONDS);

        builder.addInterceptor(commonHeaderInterceptor);
        builder.addInterceptor(commonParamInterceptor);
        builder.addInterceptor(retryIntercept);

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor);
        }


        builder.sslSocketFactory(sslSocketFactory, x509TrustManager);

        //动态baseUrl
        RetrofitUrlManager.getInstance().putDomain(NetConfig.NET_DOMAIN_NAME, NetConfig.NET_RQB_DOMAIN);
        return RetrofitUrlManager.getInstance().with(builder).build();
    }

    @Singleton
    @Provides
    @RQBRetrofit
    Retrofit providesRQBRetrofit(@RQBOkHttpClient OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(NetConfig.NET_RQB_DOMAIN)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(RQBScalarsConverterFactory.create(gson))
                .addConverterFactory(RQBGsonConverterFactory.create(gson))
                .build();
    }
}

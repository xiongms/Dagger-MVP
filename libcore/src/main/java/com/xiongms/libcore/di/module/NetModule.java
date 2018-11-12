package com.xiongms.libcore.di.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiongms.libcore.BuildConfig;
import com.xiongms.libcore.config.NetConfig;
import com.xiongms.libcore.di.qualifiers.DefaultBaseUrl;
import com.xiongms.libcore.network.GlobalHttpHandler;
import com.xiongms.libcore.network.converter.gson.RQBGsonConverterFactory;
import com.xiongms.libcore.network.converter.scalar.RQBScalarsConverterFactory;
import com.xiongms.libcore.network.interceptor.LoggingInterceptor;
import com.xiongms.libcore.network.interceptor.RetryIntercept;


import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import io.reactivex.annotations.Nullable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 提供网络相关的实例
 *
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
    SSLSocketFactory providesSSLSocketFactory(
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
    static OkHttpClient.Builder provideClientBuilder() {
        return new OkHttpClient.Builder();
    }

    @Singleton
    @Provides
    OkHttpClient providesOkHttpClient(OkHttpClient.Builder builder,
                                      LoggingInterceptor httpLoggingInterceptor,
                                      RetryIntercept retryIntercept,
                                      @Nullable final GlobalHttpHandler handler,
                                      @Nullable List<Interceptor> interceptors,
                                      Map<String, String> baseUrls,
                                      X509TrustManager x509TrustManager,
                                      SSLSocketFactory sslSocketFactory) {

        builder.connectTimeout(NetConfig.NET_TIME_OUT_CONNECT, TimeUnit.SECONDS);
        builder.readTimeout(NetConfig.NET_TIME_OUT_READ, TimeUnit.SECONDS);
        builder.writeTimeout(NetConfig.NET_TIME_OUT_WRITE, TimeUnit.SECONDS);

        if (handler != null) {
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    return chain.proceed(handler.onHttpRequestBefore(chain, chain.request()));
                }
            });
        }

        if (interceptors != null) {//如果外部提供了interceptor的集合则遍历添加
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        builder.addInterceptor(retryIntercept);

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor);
        }

        builder.sslSocketFactory(sslSocketFactory, x509TrustManager);

        //动态baseUrl
        for (String name : baseUrls.keySet()) {
            RetrofitUrlManager.getInstance().putDomain(name, baseUrls.get(name));
        }
        return RetrofitUrlManager.getInstance().with(builder).build();
    }


    @Singleton
    @Provides
    static Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    Retrofit providesRQBRetrofit(Retrofit.Builder retrofitBuilder, @DefaultBaseUrl String defaultBaseUrl, OkHttpClient okHttpClient, Gson gson) {
        return retrofitBuilder
                .client(okHttpClient)
                .baseUrl(defaultBaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(RQBScalarsConverterFactory.create(gson))
                .addConverterFactory(RQBGsonConverterFactory.create(gson))
                .build();
    }
}

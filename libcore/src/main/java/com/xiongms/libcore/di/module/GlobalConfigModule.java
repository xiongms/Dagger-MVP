/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiongms.libcore.di.module;

import android.support.annotation.Nullable;

import com.xiongms.libcore.config.NetConfig;
import com.xiongms.libcore.di.qualifiers.DefaultBaseUrl;
import com.xiongms.libcore.network.GlobalHttpHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;

/**
 * 框架的建造者模式 向框架中注入外部配置的自定义参数
 *
 */
/**
 *
 * @author xiongms
 * @time 2018-11-08 14:09
 */
@Module
public class GlobalConfigModule {
    private String mBaseUrl;
    private Map<String, String> mBaseUrls;
    private GlobalHttpHandler mHandler;
    private List<Interceptor> mInterceptors;

    public GlobalConfigModule(Builder builder) {
        this.mBaseUrl = builder.baseUrl;
        this.mBaseUrls = builder.baseUrls;
        this.mHandler = builder.handler;
        this.mInterceptors = builder.interceptors;
    }

    public static Builder builder() {
        return new Builder();
    }


    /**
     * 提供 BaseUrl,默认使用 <"https://api.github.com/">
     * @return
     */
    @Singleton
    @Provides
    @DefaultBaseUrl
    String provideBaseUrl() {
        return this.mBaseUrl;
    }

    /**
     * 提供RetrofitUrlManager多域名预置
     *
     * @return
     */
    @Singleton
    @Provides
    Map<String, String> provideBaseUrls() {
        if (mBaseUrls == null) {
            mBaseUrls = new HashMap<>();
        }

        if(mBaseUrls.size() == 0) {
            mBaseUrls.put("DEFAULT", "https://api.github.com/");
        }
        return mBaseUrls;
    }

    @Singleton
    @Provides
    @Nullable
    List<Interceptor> provideInterceptors() {
        return mInterceptors;
    }

    /**
     * 提供处理 Http 请求和响应结果的处理类
     *
     * @return
     */
    @Singleton
    @Provides
    @Nullable
    GlobalHttpHandler provideGlobalHttpHandler() {
        return mHandler;
    }


    public static final class Builder {
        private String baseUrl;
        private Map<String, String> baseUrls;
        private GlobalHttpHandler handler;
        private List<Interceptor> interceptors;

        public Builder() {
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder baseUrls(Map<String, String> baseUrls) {
            this.baseUrls = baseUrls;
            return this;
        }

        public Builder globalHttpHandler(GlobalHttpHandler handler) {//用来处理http响应结果
            this.handler = handler;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {//动态添加任意个interceptor
            if (interceptors == null)
                interceptors = new ArrayList<>();
            this.interceptors.add(interceptor);
            return this;
        }

        public GlobalConfigModule build() {
            return new GlobalConfigModule(this);
        }

    }
}

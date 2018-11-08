package com.xiongms.libcore.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 全局处理Http请求和响应
 * @author xiongms
 * @time 2018-11-08 13:41
 */
public interface GlobalHttpHandler {

    Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) throws IOException;

    Request onHttpRequestBefore(Interceptor.Chain chain, Request request) throws IOException;


    //空实现
    GlobalHttpHandler EMPTY = new GlobalHttpHandler() {
        @Override
        public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
            //不管是否处理,都必须将response返回出去
            return response;
        }

        @Override
        public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
            //不管是否处理,都必须将request返回出去
            return request;
        }
    };
}

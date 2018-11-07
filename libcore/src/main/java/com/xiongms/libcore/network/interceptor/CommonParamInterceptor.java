package com.xiongms.libcore.network.interceptor;

import android.os.Build;

import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.utils.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 添加公共参数
 * @author xiongms
 * @time 2018-08-16 14:11
 */
public class CommonParamInterceptor implements Interceptor {
    private final Charset UTF8 = Charset.forName("UTF-8");

    public CommonParamInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        HttpUrl.Builder modifiedUrlBuilder = request.url().newBuilder();
        HttpUrl modifiedUrl;
        String method = request.method();
        if ("GET".equals(method)) {

            modifiedUrlBuilder.addQueryParameter("appid", "rqb_android");
            modifiedUrlBuilder.addQueryParameter("cid", "rqb_android");
            modifiedUrlBuilder.addQueryParameter("sign", "");
            modifiedUrlBuilder.addQueryParameter("chn", "");
            modifiedUrlBuilder.addQueryParameter("ov", Build.MODEL + Build.VERSION.RELEASE);
            modifiedUrlBuilder.addQueryParameter("av", BaseApplication.getInstance().getEnv().appVersion());
            modifiedUrlBuilder.addQueryParameter("nt", String.valueOf(BaseApplication.getInstance().getEnv().networkOperator()));

            modifiedUrlBuilder.addQueryParameter("tkn", BaseApplication.getInstance().getEnv().appPreferencesHelper().getToken());
//            modifiedUrlBuilder.addQueryParameter("userId", String.valueOf(0));
            modifiedUrlBuilder.addQueryParameter("shopid", String.valueOf(BaseApplication.getInstance().getEnv().appPreferencesHelper().getStoreId()));

            if(StrUtil.isEmpty(modifiedUrlBuilder.build().queryParameter("phone"))) {
                modifiedUrlBuilder.addQueryParameter("phone", BaseApplication.getInstance().getEnv().appPreferencesHelper().getUserPhone());
            }
            //TODO add param
            modifiedUrl = modifiedUrlBuilder.build();

            return chain.proceed(request.newBuilder().url(modifiedUrl).build());
        } else if (request.body() instanceof FormBody) {
            Request.Builder requestBuilder = request.newBuilder();
            FormBody.Builder newFormBody = new FormBody.Builder();
            FormBody oldFormBody = (FormBody) request.body();
            if (oldFormBody != null) {
                for (int i = 0; i < oldFormBody.size(); i++) {
                    newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
                }
            }
            //TODO add param
            requestBuilder.method(request.method(), newFormBody.build());
            request = requestBuilder.build();
            return chain.proceed(request);
        } else if (request.body().contentType().subtype().equalsIgnoreCase("json")) {

            String body = null;
            if (requestBody != null) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                body = buffer.readString(charset);
            }

            String newJsonBody = "";
            try {
                JSONObject jsonObject = new JSONObject(body);
                jsonObject.put("appid", "rqb_android");
                jsonObject.put("cid", "rqb_android");
                jsonObject.put("sign", "");
                jsonObject.put("chn", "");
                jsonObject.put("ov", Build.MODEL + Build.VERSION.RELEASE);
                jsonObject.put("av", BaseApplication.getInstance().getEnv().appVersion());
                jsonObject.put("nt", String.valueOf(BaseApplication.getInstance().getEnv().networkOperator()));

                jsonObject.put("tkn", BaseApplication.getInstance().getEnv().appPreferencesHelper().getToken());
//                jsonObject.put("userId", 0);
                jsonObject.put("shopid", BaseApplication.getInstance().getEnv().appPreferencesHelper().getStoreId());


                boolean hadPhone = false;
                try {
                    hadPhone = !StrUtil.isEmpty(jsonObject.getString("phone"));
                } catch (Exception ex) {
                }
                if(!hadPhone) {
                    String phone = BaseApplication.getInstance().getEnv().appPreferencesHelper().getUserPhone();
                    jsonObject.put("phone", phone);
                }

                newJsonBody = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody1 = RequestBody.create(MediaType.parse("application/json"), newJsonBody);
            //TODO add param

            Request.Builder requestBuilder = request.newBuilder();
            requestBuilder.method(request.method(), requestBody1);
            request = requestBuilder.build();
            return chain.proceed(request);
        }
        return chain.proceed(request);
    }
}

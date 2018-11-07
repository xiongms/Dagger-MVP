package com.xiongms.libcore.network.converter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.network.exception.ApiException;
import com.xiongms.libcore.utils.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

/**
 *
 */
public class RQBGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    RQBGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            BaseBean httpStatus = gson.fromJson(response, BaseBean.class);
            if (httpStatus.getCode() == 401) {
                value.close();
                throw new ApiException(httpStatus.getMsg(), httpStatus.getCode());
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}


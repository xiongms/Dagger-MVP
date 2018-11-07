package com.xiongms.libcore.network.converter.scalar;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 *
 */
public class RQBScalarRequestBodyConverter<T> implements Converter<T, RequestBody> {
    static final RQBScalarRequestBodyConverter<Object> INSTANCE = new RQBScalarRequestBodyConverter<>();
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain; charset=UTF-8");

    private RQBScalarRequestBodyConverter() {
    }

    @Override public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
    }
}

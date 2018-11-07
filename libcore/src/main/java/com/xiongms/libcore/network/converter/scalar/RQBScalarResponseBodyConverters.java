package com.xiongms.libcore.network.converter.scalar;

import com.google.gson.Gson;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.network.exception.ApiException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class RQBScalarResponseBodyConverters {


    private RQBScalarResponseBodyConverters() {

    }

    static final class StringResponseBodyConverter implements Converter<ResponseBody, String> {

        public Gson gson;

        private static RQBScalarResponseBodyConverters.StringResponseBodyConverter INSTANCE;

        static RQBScalarResponseBodyConverters.StringResponseBodyConverter getInstance(Gson gson) {
            if(INSTANCE == null) {
                INSTANCE = new StringResponseBodyConverter(gson);
            }
            return INSTANCE;
        }

        public StringResponseBodyConverter(Gson gson) {
            this.gson = gson;
        }

        @Override
        public String convert(ResponseBody value) throws IOException {

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

            return response;
        }
    }

    static final class BooleanResponseBodyConverter implements Converter<ResponseBody, Boolean> {
        static final RQBScalarResponseBodyConverters.BooleanResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.BooleanResponseBodyConverter();

        @Override
        public Boolean convert(ResponseBody value) throws IOException {
            return Boolean.valueOf(value.string());
        }
    }

    static final class ByteResponseBodyConverter implements Converter<ResponseBody, Byte> {
        static final RQBScalarResponseBodyConverters.ByteResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.ByteResponseBodyConverter();

        @Override
        public Byte convert(ResponseBody value) throws IOException {
            return Byte.valueOf(value.string());
        }
    }

    static final class CharacterResponseBodyConverter implements Converter<ResponseBody, Character> {
        static final RQBScalarResponseBodyConverters.CharacterResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.CharacterResponseBodyConverter();

        @Override
        public Character convert(ResponseBody value) throws IOException {
            String body = value.string();
            if (body.length() != 1) {
                throw new IOException(
                        "Expected body of length 1 for Character conversion but was " + body.length());
            }
            return body.charAt(0);
        }
    }

    static final class DoubleResponseBodyConverter implements Converter<ResponseBody, Double> {
        static final RQBScalarResponseBodyConverters.DoubleResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.DoubleResponseBodyConverter();

        @Override
        public Double convert(ResponseBody value) throws IOException {
            return Double.valueOf(value.string());
        }
    }

    static final class FloatResponseBodyConverter implements Converter<ResponseBody, Float> {
        static final RQBScalarResponseBodyConverters.FloatResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.FloatResponseBodyConverter();

        @Override
        public Float convert(ResponseBody value) throws IOException {
            return Float.valueOf(value.string());
        }
    }

    static final class IntegerResponseBodyConverter implements Converter<ResponseBody, Integer> {
        static final RQBScalarResponseBodyConverters.IntegerResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.IntegerResponseBodyConverter();

        @Override
        public Integer convert(ResponseBody value) throws IOException {
            return Integer.valueOf(value.string());
        }
    }

    static final class LongResponseBodyConverter implements Converter<ResponseBody, Long> {
        static final RQBScalarResponseBodyConverters.LongResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.LongResponseBodyConverter();

        @Override
        public Long convert(ResponseBody value) throws IOException {
            return Long.valueOf(value.string());
        }
    }

    static final class ShortResponseBodyConverter implements Converter<ResponseBody, Short> {
        static final RQBScalarResponseBodyConverters.ShortResponseBodyConverter INSTANCE = new RQBScalarResponseBodyConverters.ShortResponseBodyConverter();

        @Override
        public Short convert(ResponseBody value) throws IOException {
            return Short.valueOf(value.string());
        }
    }
}

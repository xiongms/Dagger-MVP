/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiongms.libcore.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

// TODO: Auto-generated Javadoc

/**
 * 
 * @author xiongms
 * @time 2018-08-27 11:33
 */
public class JsonUtil {

    private static GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls();

    /**
     * 描述：将对象转化为json.
     *
     * @return
     */
    public static String toJson(Object src) {
        String json = "";
        try {
            Gson gson = gsonBuilder.create();
            json = gson.toJson(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JsonElement toJsonElement(Object src) {
        JsonElement json = null;
        try {
            Gson gson = gsonBuilder.create();
            json = gson.toJsonTree(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 描述：将列表转化为json.
     *
     * @param list
     * @return
     */
    public static String toJson(List<?> list) {
        String json = null;
        try {
            if(list != null) {
                Gson gson = gsonBuilder.create();
                json = gson.toJson(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }


    /**
     * 描述：将json转化为列表.
     *
     * @param json
     * @param typeToken new TypeToken<ArrayList<?>>() {};
     * @return
     */
    public static <T> T fromJson(String json, TypeToken<?> typeToken) {
        if (json == null || json.trim().length() == 0) {
            return null;
        }
        try {
            Type type = typeToken.getType();
            Gson gson = gsonBuilder.create();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(JsonElement json, TypeToken<?> typeToken) {
        if (json == null) {
            return null;
        }
        try {
            Type type = typeToken.getType();
            Gson gson = gsonBuilder.create();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 描述：将json转化为对象.
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        Object obj = null;
        try {
            Gson gson = gsonBuilder.create();
            obj = gson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) obj;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        List<User> list = (List<User>) fromJson("[{id:1,name:22},{id:2,name:33}]", new TypeToken<ArrayList<User>>() {
        });
        System.out.println(list.size());
        for (User u : list) {
            System.out.println(u.getName());
        }

        User u = (User) fromJson("{id:1,name:22}", User.class);
        System.out.println(u.getName());
    }

    static class User {
        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static void setGsonBuilderDateFormat(String format) {
        gsonBuilder.setDateFormat(format);
    }

    public static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringNullAdapter();
        }
    }

    public static class StringNullAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            // TODO Auto-generated method stub
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            // TODO Auto-generated method stub
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

}

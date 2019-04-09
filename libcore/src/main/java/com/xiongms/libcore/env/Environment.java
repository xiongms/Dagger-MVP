package com.xiongms.libcore.env;

import android.os.Parcelable;

import com.google.gson.Gson;
import com.xiongms.libcore.utils.AppPreferencesHelper;

import auto.parcel.AutoParcel;
import retrofit2.Retrofit;

@AutoParcel
public abstract class Environment implements Parcelable {

    public abstract Gson gson();

    public abstract Retrofit rqbRetrofit();

    public abstract AppPreferencesHelper appPreferencesHelper();

    @AutoParcel.Builder
    public abstract static class Builder {


        public abstract Builder gson(Gson __);

        public abstract Builder rqbRetrofit(Retrofit __);

        public abstract Builder appPreferencesHelper(AppPreferencesHelper __);

        public abstract Environment build();
    }

    public static Builder builder() {
        return new AutoParcel_Environment.Builder();
    }
}

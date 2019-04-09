package com.xiongms.libcore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author xiongms
 * @time 2018-11-14 16:55
 */
public class AppPreferencesHelper {

    private static AppPreferencesHelper appPreferencesHelper = null;

    public static AppPreferencesHelper getInstance(Context context) {
        if (appPreferencesHelper == null) {
            appPreferencesHelper = new AppPreferencesHelper(context);
        }

        return appPreferencesHelper;
    }

    public final static String KEY_SP_TOKEN = "key_sp_token";

    private final String prefFileName = "App_Preferences";

    private final SharedPreferences mPrefs;

    private AppPreferencesHelper(Context context) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }


    public void saveModel(Object object) {
        saveModel("", object);
    }

    public synchronized void saveModel(String tag, Object object) {
        if (object != null) {
            String sInfo = JsonUtil.toJson(object);
            if (!TextUtils.isEmpty(sInfo)) {
                mPrefs.edit().putString(tag + object.getClass().getSimpleName(), sInfo).apply();
            }
        }
    }

    public synchronized <T> T getModel(Class<T> classz) {
        return getModel("", classz);
    }

    public synchronized <T> T getModel(String tag, Class<T> classz) {
        String sInfo = mPrefs.getString(tag + classz.getSimpleName(), "");
        if (!TextUtils.isEmpty(sInfo)) {
            return JsonUtil.fromJson(sInfo, classz);
        }
        return null;
    }

    public synchronized void removeModel(Class classz) {
        removeModel("", classz);
    }

    public synchronized void removeModel(String tag, Class classz) {
        mPrefs.edit().putString(tag + classz.getSimpleName(), null).apply();
    }

    public void setToken(String token) {
        if (!TextUtils.isEmpty(token)) {
            mPrefs.edit().putString(KEY_SP_TOKEN, token).apply();
        }
    }

    public String getToken() {
        String token = mPrefs.getString(KEY_SP_TOKEN, "");
        if (!TextUtils.isEmpty(token)) {
            return token;
        }
        return "";
    }

    public void removeAll() {
        mPrefs.edit().clear().apply();
    }
}
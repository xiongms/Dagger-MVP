package com.xiongms.libcore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.xiongms.libcore.bean.Store;
import com.xiongms.libcore.bean.User;
import com.xiongms.libcore.di.qualifiers.ApplicationContext;
import com.xiongms.libcore.di.qualifiers.PreferenceInfo;

import javax.inject.Inject;

/**
 * 
 * @author xiongms
 * @time 2018-08-16 16:55
 */
public class AppPreferencesHelper {


    public final static String KEY_SP_USER_PHONE = "key_sp_user_phone";

    public final static String KEY_SP_USER_INFO = "key_sp_user_info";

    public final static String KEY_SP_STORE_INFO = "key_sp_store_info";

    public final static String KEY_SP_PRE_STORE_ID = "key_sp_pre_store_id";

    public final static String KEY_SP_PRE_CHECK_UPDATE_DATE = "key_sp_pre_check_update_date";


    private final SharedPreferences mPrefs;

    private final Gson mGson;

    @Inject
    AppPreferencesHelper(@ApplicationContext Context context,
                         @PreferenceInfo String prefFileName) {
        mGson = JsonUtil.gson;
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    public void setUserPhone(String phone) {
        mPrefs.edit().putString(KEY_SP_USER_PHONE, phone).apply();
    }

    public String getUserPhone() {
        return mPrefs.getString(KEY_SP_USER_PHONE, "");
    }

    public void setUser(User userInfo) {
        if(userInfo != null) {
            setUserPhone(userInfo.getPhone());
        }
        String sUserInfo = mGson.toJson(userInfo);
        mPrefs.edit().putString(KEY_SP_USER_INFO, sUserInfo).apply();
    }

    public User getUser() {
        String sUser = mPrefs.getString(KEY_SP_USER_INFO, "");
        User user = mGson.fromJson(sUser, User.class);
        if(user == null)
            user = new User();
        return user;
    }

    public void setStore(Store store) {
        String sStore = mGson.toJson(store);
        mPrefs.edit().putString(KEY_SP_STORE_INFO, sStore).apply();
    }

    public Store getStore() {
        String sStore = mPrefs.getString(KEY_SP_STORE_INFO, "");
        Store store = mGson.fromJson(sStore, Store.class);
        if(store == null)
            store = new Store();
        return store;
    }

    public String getToken() {
        User user = getUser();
        return user.getTkn();
    }

    public int getStoreId() {
        Store store = getStore();
        return store.getStoreId();
    }

    public void setPreStoreId(int id) {
        mPrefs.edit().putInt(KEY_SP_PRE_STORE_ID, id).apply();
    }

    public int getPreStoreId() {
        return mPrefs.getInt(KEY_SP_PRE_STORE_ID, -1);
    }



    public void setCheckUpdateDate(String date) {
        mPrefs.edit().putString(KEY_SP_PRE_CHECK_UPDATE_DATE, date).apply();
    }

    public String getCheckUpdateDate() {
        return mPrefs.getString(KEY_SP_PRE_CHECK_UPDATE_DATE, "");
    }

    public void removeAll() {
        mPrefs.edit().clear().apply();
    }
}

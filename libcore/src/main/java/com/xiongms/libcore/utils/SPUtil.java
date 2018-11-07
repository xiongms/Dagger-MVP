package com.xiongms.libcore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class SPUtil {
    private static final String SETTING = "app_setting_";

    private static SharedPreferences getSharedPreferences(boolean isPublic) {
        SharedPreferences sharedPreferences = null;
        try {
            String name = null;
            if (!isPublic) {
                name = "";//如果非公共，此处可使用户标识，如：用户名
            }
            if (!TextUtils.isEmpty(name)) {
                name = SETTING + name;
            } else {
                name = SETTING;
            }
            sharedPreferences = ActivityUtil.getInstance().getCurrentActivity().getSharedPreferences(name, Context.MODE_PRIVATE);
        } catch (Exception e) {
        }
        return sharedPreferences;
    }

    /**
     * 保存基本类型缓存 默认存储方式为不共用 只能当前登录用户使用
     *
     * @param key   键名称
     * @param value 值名称
     * @return 是否保存成功
     */
    public static boolean setValue(String key, Object value) {
        return setValue(key, value, false);
    }

    /**
     * 保存基本类型缓存
     *
     * @param key      键名称
     * @param value    值名称
     * @param isPublic 存储方式 true表示该缓存可以所有用户使用  false表示该缓存只能当前登录用户使用
     * @return 是否保存成功
     */
    public static boolean setValue(String key, Object value, boolean isPublic) {
        try {
            Editor edit = getSharedPreferences(isPublic).edit();
            if (value instanceof String) {
                return edit.putString(key, (String) value).commit();
            } else if (value instanceof Boolean) {
                return edit.putBoolean(key, (Boolean) value).commit();
            } else if (value instanceof Float) {
                return edit.putFloat(key, (Float) value).commit();
            } else if (value instanceof Integer) {
                return edit.putInt(key, (Integer) value).commit();
            } else if (value instanceof Long) {
                return edit.putLong(key, (Long) value).commit();
            }
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 从当前登录用户缓存中获取boolean值
     *
     * @param key 键名称
     * @return boolean
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 从当前登录用户缓存中获取boolean值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @return boolean
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return getBoolean(key, defValue, false);
    }

    /**
     * 获取boolean值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @param isPublic true-->从公共缓存中获取  false-->从当前登录用户缓存中获取
     * @return boolean
     */
    public static boolean getBoolean(String key, boolean defValue, boolean isPublic) {
        SharedPreferences sharedPreferences = getSharedPreferences(isPublic);
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defValue);
        } else {
            return defValue;
        }
    }

    /**
     * 从当前登录用户缓存中获取String值
     *
     * @param key 键名称
     * @return String
     */
    public static String getString(String key) {
        return getString(key, "");
    }

    /**
     * 从当前登录用户缓存中获取String值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @return String
     */
    public static String getString(String key, String defValue) {
        return getString(key, defValue, false);
    }

    /**
     * 获取String值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @param isPublic true-->从公共缓存中获取  false-->从当前登录用户缓存中获取
     * @return String
     */
    public static String getString(String key, String defValue, boolean isPublic) {
        SharedPreferences sharedPreferences = getSharedPreferences(isPublic);
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, defValue);
        } else {
            return defValue;
        }
    }

    /**
     * 从当前登录用户缓存中获取float值
     *
     * @param key 键名称
     * @return float
     */
    public static float getFloat(String key) {
        return getFloat(key, 0f);
    }

    /**
     * 从当前登录用户缓存中获取float值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @return float
     */
    public static float getFloat(String key, float defValue) {
        return getFloat(key, defValue, false);
    }

    /**
     * 获取float值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @param isPublic true-->从公共缓存中获取  false-->从当前登录用户缓存中获取
     * @return float
     */
    public static float getFloat(String key, float defValue, boolean isPublic) {
        SharedPreferences sharedPreferences = getSharedPreferences(isPublic);
        if (sharedPreferences != null) {
            return sharedPreferences.getFloat(key, defValue);
        } else {
            return defValue;
        }
    }

    /**
     * 从当前登录用户缓存中获取int值
     *
     * @param key 键名称
     * @return int
     */
    public static int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 从当前登录用户缓存中获取int值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @return int
     */
    public static int getInt(String key, int defValue) {
        return getInt(key, defValue, false);
    }

    /**
     * 获取int值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @param isPublic true-->从公共缓存中获取  false-->从当前登录用户缓存中获取
     * @return int
     */
    public static int getInt(String key, int defValue, boolean isPublic) {
        SharedPreferences sharedPreferences = getSharedPreferences(isPublic);
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, defValue);
        } else {
            return defValue;
        }
    }

    /**
     * 从当前登录用户缓存中获取long值
     *
     * @param key 键名称
     * @return long
     */
    public static long getLong(String key) {
        return getLong(key, 0);
    }

    /**
     * 从当前登录用户缓存中获取long值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @return long
     */
    public static long getLong(String key, int defValue) {
        return getLong(key, defValue, false);
    }

    /**
     * 获取long值
     *
     * @param key      键名称
     * @param defValue 默认值
     * @param isPublic true-->从公共缓存中获取  false-->从当前登录用户缓存中获取
     * @return long
     */
    public static long getLong(String key, int defValue, boolean isPublic) {
        SharedPreferences sharedPreferences = getSharedPreferences(isPublic);
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(key, defValue);
        } else {
            return defValue;
        }
    }
}
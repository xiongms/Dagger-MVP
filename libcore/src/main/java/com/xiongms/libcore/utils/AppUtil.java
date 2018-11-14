package com.xiongms.libcore.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.inputmethod.InputMethodManager;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class AppUtil {

    /**
     * 描述：打开并安装文件.
     *
     * @param context the context
     * @param file    文件路径
     */
    public static void shareFile(Context context, File file, String fileProvider) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_STREAM, FileUtil.getUri(context, file, fileProvider));
        intent.setType("*/*");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(Intent.createChooser(intent, file.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 描述：打开并安装文件.
     *
     * @param context the context
     * @param file    apk文件路径
     */
    public static void installApk(Context context, File file, String fileProvider) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(FileUtil.getUri(context, file, fileProvider),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 描述：卸载程序.
     *
     * @param context     the context
     * @param packageName 包名
     */
    public static void uninstallApk(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param context   the context
     * @param className 判断的服务名字 "com.xxx.xx..XXXService"
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator<ActivityManager.RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            ActivityManager.RunningServiceInfo si = (ActivityManager.RunningServiceInfo) l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 停止服务.
     *
     * @param context   the context
     * @param className the class name
     * @return true, if successful
     */
    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }


    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Gps是否打开
     * 需要<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />权限
     *
     * @param context the context
     * @return true, if is gps enabled
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /**
     * 判断当前网络是否是移动数据网络.
     *
     * @param context the context
     * @return boolean
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        //DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
        //DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }

    /**
     * 打开键盘.
     *
     * @param context the context
     */
    public static void showSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭键盘事件.
     *
     * @param context the context
     */
    public static void closeSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获取包信息.
     *
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取AppName
     *
     * @param context the context
     */
    public static String getAppName(Context context) {
        String name = "";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                name = context.getString(info.applicationInfo.labelRes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 获取versionName
     *
     * @param context the context
     */
    public static String getAppVersionName(Context context) {
        String name = "";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                name = info.versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 获取versionCode
     *
     * @param context the context
     */
    public static int getPackageVersionCode(Context context) {
        int code = 0;
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                code = info.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 描述：根据进程名返回应用程序.
     *
     * @param context
     * @param processName
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context, String processName) {
        if (processName == null) {
            return null;
        }

        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }

    /**
     * 描述：获取可用内存.
     *
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {
        //获取android当前可用内存大小
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        //当前系统可用内存 ,将获得的内存大小规格化
        return memoryInfo.availMem;
    }

    /**
     * 获取mac地址.
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info.getMacAddress() == null) {
            return null;
        } else {
            return info.getMacAddress();
        }
    }

    /**
     * 获取SSID地址.
     *
     * @param context
     * @return
     */
    public static String getSSID(Context context) {

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info.getSSID() == null) {
            return null;
        } else {
            return info.getSSID();
        }
    }

    /**
     * 获取IMSI.
     *
     * @return
     */
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getSubscriberId() == null) {
            return null;
        } else {
            return telephonyManager.getSubscriberId();
        }
    }

    /**
     * 获取IMEI.
     *
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getDeviceId() == null) {
            return null;
        } else {
            return telephonyManager.getDeviceId();
        }
    }

    /**
     * 手机号码
     *
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getLine1Number() == null || telephonyManager.getLine1Number().length() < 11) {
            return null;
        } else {
            return telephonyManager.getLine1Number();
        }
    }

    /**
     * 获取当前的运营商
     *
     * @param context
     * @return 运营商名字
     */
    public static int getOperator(Context context) {
        int ProvidersName = 0;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = telephonyManager.getSubscriberId();
            if (IMSI != null) {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                    ProvidersName = 1;
                } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
                    ProvidersName = 2;
                } else if (IMSI.startsWith("46003")) {
                    ProvidersName = 3;
                }
            }
        } catch (Exception ex) {
        }
        return ProvidersName;
    }

}

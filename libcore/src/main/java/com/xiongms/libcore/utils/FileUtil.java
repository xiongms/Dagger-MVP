package com.xiongms.libcore.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * 文件工具类
 * @author xiongms
 * @time 2018-08-24 17:49
 */
public class FileUtil {


    /**
     * 描述：获取Uri
     * Android N与之前的方式有区别
     */
    public static Uri getUri(Context context, File file, String fileProvider) {
        if (file == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || !isCanUseSD(context)) {
            Uri uri = FileProvider.getUriForFile(context, fileProvider, file);
            return uri;
        } else {
            Uri uri = Uri.fromFile(file);
            return uri;
        }
    }


    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    public static boolean isCanUseSD(Context context) {
        return hasSDWritePermission(context);
    }

    private static boolean hasSDWritePermission(Context context) {
        boolean canUseSD = false;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (checkCallPhonePermission == PackageManager.PERMISSION_GRANTED) {
                        canUseSD = true;
                    }
                } else {
                    canUseSD = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canUseSD;
    }

    /**
     * 获取扩展名
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

}

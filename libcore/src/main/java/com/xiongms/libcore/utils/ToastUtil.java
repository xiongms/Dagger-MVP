package com.xiongms.libcore.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xiongms.libcore.BaseApplication;

/**
 * Toast工具类 创建通用Toast
 */
public class ToastUtil {

    private static final String TAG = "ToastUtil1";

    private static Toast mToast = null;

    /**
     * 在application中初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public static void show(String text) {
        if (mToast == null) {
            Log.w(TAG, "mToast is null");
            return;
        }

        // APP处于前台时才显示Toast
        if (BaseApplication.getInstance().mIsForeground) {
            View view = mToast.getView();
            if(view != null && view.getParent() != null) {
                return;
            }
            mToast.setText(text);
            mToast.show();
        }
    }
}

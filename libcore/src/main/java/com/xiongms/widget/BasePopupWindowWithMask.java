package com.xiongms.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by kk on 2017/7/22.
 */

public class BasePopupWindowWithMask extends Solve7PopupWindow {
    protected Context context;
    private WindowManager windowManager;
    private View maskView;

    public BasePopupWindowWithMask(Context context, int width, int height) {
        super(width, height);
        this.context = context;
        init();
    }

    private void init() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
    }

    @Override
    public void showAsDropDown(View anchor) {
        addMask(anchor.getWindowToken());
        super.showAsDropDown(anchor);
    }

    private void addMask(IBinder token) {
        if(maskView == null) {
            WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
            wl.width = WindowManager.LayoutParams.MATCH_PARENT;
            wl.height = WindowManager.LayoutParams.MATCH_PARENT;
            wl.format = PixelFormat.TRANSLUCENT;//不设置这个弹出框的透明遮罩显示为黑色
            wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;//该Type描述的是形成的窗口的层级关系
            wl.token = token;//获取当前Activity中的View中的token,来依附Activity
            maskView = new View(context);
            maskView.setBackgroundColor(0x7f000000);
            maskView.setFitsSystemWindows(false);
            maskView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        removeMask();
                        return true;
                    }
                    return false;
                }
            });
            /**
             * 通过WindowManager的addView方法创建View，产生出来的View根据WindowManager.LayoutParams属性不同，效果也就不同了。
             * 比如创建系统顶级窗口，实现悬浮窗口效果！
             */
            windowManager.addView(maskView, wl);
        }
    }

    private void removeMask() {
        if (null != maskView) {
            windowManager.removeViewImmediate(maskView);
            maskView = null;
        }
    }

    @Override
    public void dismiss() {
        removeMask();
        super.dismiss();
    }
}
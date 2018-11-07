package com.xiongms.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * 兼容Android 7.0之后出现showAsDropDown无法生效的问题
 * Created by xiongms on 2017/5/25.
 */

public class Solve7PopupWindow extends PopupWindow {

    public Solve7PopupWindow(Context context) {
        super(context);
    }

    public Solve7PopupWindow(int width, int height) {
        super(width, height);
    }

    public Solve7PopupWindow(View mMenuView, int matchParent, int matchParent1) {
        super(mMenuView, matchParent, matchParent1);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24
                && getHeight() == ViewGroup.LayoutParams.MATCH_PARENT) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= 24
                && getHeight() == ViewGroup.LayoutParams.MATCH_PARENT) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }
}

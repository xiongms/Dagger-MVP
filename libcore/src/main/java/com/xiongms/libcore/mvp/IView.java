package com.xiongms.libcore.mvp;

import android.content.Context;

/**
 * 
 * @author xiongms
 * @time 2018-08-22 11:13
 */
public interface IView {
    Context getContext();

    void showLoading(boolean isDialog);

    void hideLoading();
}

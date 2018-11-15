package com.xiongms.mod_main.mvp.main;

import com.xiongms.libcore.mvp.IPresenter;
import com.xiongms.libcore.mvp.IView;

/**
 *
 */
public interface Contract {

    interface View extends IView {
        void setText(String name);
    }

    interface Presenter extends IPresenter<View> {

        void initData();

        void clickTextView();
    }
}

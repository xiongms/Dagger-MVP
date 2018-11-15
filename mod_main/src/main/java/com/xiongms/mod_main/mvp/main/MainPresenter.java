package com.xiongms.mod_main.mvp.main;

import com.xiongms.libcore.mvp.BasePresenter;
import com.xiongms.libcore.utils.ToastUtil;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 */
public class MainPresenter extends BasePresenter<Contract.View> implements Contract.Presenter {

    @Inject
    @Named("PresenterClassName")
    public String mClassName;

    @Inject
    public MainPresenter() {

    }

    @Override
    public void initData() {
        mRootView.setText(mClassName);
    }

    @Override
    public void clickTextView() {
        ToastUtil.show("click:" + mClassName);
    }
}

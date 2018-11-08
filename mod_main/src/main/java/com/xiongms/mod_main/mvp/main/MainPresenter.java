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
        super();
    }

    @Override
    public void onAttach(Contract.View rootView) {
        super.onAttach(rootView);
        rootView.setText(mClassName);
    }

    @Override
    public void clickTextView() {
        ToastUtil.show("click:" + mClassName);
    }
}

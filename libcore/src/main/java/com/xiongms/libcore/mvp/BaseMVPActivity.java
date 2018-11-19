package com.xiongms.libcore.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiongms.libcore.base.BaseActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * MVP中Activity基类
 * 自动注入Presenter对象，并完成绑定与解绑操作
 * @author xiongms
 * @time 2018-11-19 14:26
 */
public abstract class BaseMVPActivity<P extends IPresenter> extends BaseActivity {


    @Inject
    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        mPresenter.onAttach(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDetach();//释放资源
        mPresenter = null;
    }
}

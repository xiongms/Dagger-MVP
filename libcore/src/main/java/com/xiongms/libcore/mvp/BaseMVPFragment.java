package com.xiongms.libcore.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiongms.libcore.base.BaseFragment;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * MVP中Fragment基类
 *
 * @author xiongms
 * @time 2018-11-19 14:28
 */
public abstract class BaseMVPFragment<P extends IPresenter> extends BaseFragment {

    @Inject
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //绑定View
        mPresenter.onAttach(this);

        return onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDetach();//释放资源
        this.mPresenter = null;
    }
}

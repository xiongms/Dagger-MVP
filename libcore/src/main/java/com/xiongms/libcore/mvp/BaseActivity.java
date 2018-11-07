package com.xiongms.libcore.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiongms.libcore.utils.ActivityUtil;
import com.xiongms.libcore.utils.LoadingDialogUtil;
import com.xiongms.statusbar.StatusBarHelper;


import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Activity的基类
 *
 * @author xiongms
 * @time 2018-08-22 11:31
 */
public abstract class BaseActivity<P extends IPresenter> extends RxAppCompatActivity implements IView, HasFragmentInjector, HasSupportFragmentInjector {

    protected Context mContext;

    private LoadingDialogUtil mLoadingDialogUtil;

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;
    @Inject
    DispatchingAndroidInjector<android.app.Fragment> frameworkFragmentInjector;

    @Inject
    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        mContext = this;
        ActivityUtil.getInstance().addActivity(this);

        mLoadingDialogUtil = new LoadingDialogUtil(this);
        try {
            int layoutResID = initView(savedInstanceState);
            //如果initView返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
            if (layoutResID != 0) {
                setContentView(layoutResID);
                //绑定到butterknife
                mUnbinder = ButterKnife.bind(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData(savedInstanceState);
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    public AndroidInjector<android.app.Fragment> fragmentInjector() {
        return frameworkFragmentInjector;
    }

    /**
     * 初始化 布局资源文件ID, 如果 {@link #initView(Bundle)} 返回 0, 框架则不会调用 {@link Activity#setContentView(int)}
     *
     * @param savedInstanceState
     * @return
     */
    public abstract int initView(@Nullable Bundle savedInstanceState);

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    public abstract void initData(@Nullable Bundle savedInstanceState);

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onDestroy() {
        ActivityUtil.getInstance().removeActivity(this);
        super.onDestroy();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
        if (mPresenter != null) mPresenter.onDetach();//释放资源
        this.mPresenter = null;

        if (mLoadingDialogUtil != null) {
            mLoadingDialogUtil.destoryLoadingDialog();
            mLoadingDialogUtil = null;
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    public void setStatusBarColor(int color) {
        try {
            StatusBarHelper.setStatusBarColor(this, color);
        } catch (Exception ex) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void showLoadingDialog() {
        if (mLoadingDialogUtil != null) {
            mLoadingDialogUtil.showLoadingDialog(this);
        }
    }

    public void cancelLoadingDialog() {
        if (mLoadingDialogUtil != null) {
            mLoadingDialogUtil.cancelLoadingDialog();
        }
    }

    public void setLoaddingDialogText(String text) {
        if (mLoadingDialogUtil != null) {
            mLoadingDialogUtil.setLoadingText(text);
        }
    }

    public void showLoading(boolean isDialog) {
        if (isDialog) {
            showLoadingDialog();
        }
    }

    public void hideLoading() {
        cancelLoadingDialog();
    }
}

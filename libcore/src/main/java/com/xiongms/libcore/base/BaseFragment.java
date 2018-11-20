package com.xiongms.libcore.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.mvp.IView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author xiongms
 * @time 2018-08-17 15:55
 */
public abstract class BaseFragment extends RxFragment implements IView {
    protected final String TAG = this.getClass().getSimpleName();

    protected View mRootView;


    private Unbinder mUnbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = initView(inflater, container, savedInstanceState);

        try {
            if (mRootView != null) {
                //绑定到butterknife
                mUnbinder = ButterKnife.bind(this, mRootView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initData(savedInstanceState);

        return mRootView;
    }


    /**
     * 初始化 View
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public abstract View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    public abstract void initData(@Nullable Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = BaseApplication.refWatcher;
        if(refWatcher != null) refWatcher.watch(this);
    }

    @Override
    public void showLoading(boolean isDialog) {
        if (isDialog) {
            if (getActivity() instanceof BaseActivity) {
                ((BaseActivity) getActivity()).showLoadingDialog();
            }
        }
    }

    @Override
    public void hideLoading() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideLoading();
        }
    }
}

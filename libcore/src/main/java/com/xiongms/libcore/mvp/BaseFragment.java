package com.xiongms.libcore.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author xiongms
 * @time 2018-08-17 15:55
 */
public abstract class BaseFragment<P extends IPresenter> extends RxFragment implements IView {
    protected final String TAG = this.getClass().getSimpleName();

    protected View mRootView;

    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null

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
        if (mPresenter != null) mPresenter.onDetach();//释放资源
        this.mPresenter = null;
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

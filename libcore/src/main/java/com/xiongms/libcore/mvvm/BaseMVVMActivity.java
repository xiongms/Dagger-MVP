package com.xiongms.libcore.mvvm;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiongms.libcore.base.BaseActivity;
import com.xiongms.libcore.enums.LoadingStateEnum;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class BaseMVVMActivity<T extends BaseViewModel> extends BaseActivity {


    protected T mViewModel;

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        if (mViewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            mViewModel = (T) ViewModelProviders.of(this).get(modelClass);
        }

        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(mViewModel);
        //注入RxLifecycle生命周期
        mViewModel.injectLifecycleProvider(this);

        registorUIChangeLiveDataCallBack();

        init(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除ViewModel生命周期感应
        getLifecycle().removeObserver(mViewModel);
    }

    public abstract void init(Bundle savedInstanceState);

    //注册ViewModel与View的契约UI回调事件
    private void registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        mViewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showLoading(true);
            }
        });
        //加载对话框消失
        mViewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                hideLoading();
            }
        });
        //跳入新页面
        mViewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String router = (String) params.get(BaseViewModel.ParameterField.ROUTER);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                startActivity(router, bundle);
            }
        });
        //跳入新页面
        mViewModel.getUC().getStartActivityForIntentEvent().observe(this, new Observer<Intent>() {
            @Override
            public void onChanged(@Nullable Intent intent) {
                startActivity(intent);
            }
        });
        //关闭界面
        mViewModel.getUC().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                finish();
            }
        });
        //关闭上一层
        mViewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                onBackPressed();
            }
        });
        //消息
        mViewModel.getUC().getBusEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String event = (String) params.get(BaseViewModel.ParameterField.EVENT);
                Object object = params.get(BaseViewModel.ParameterField.OBJECT);
                onBusEvent(event, object);
            }
        });
        //加载状态
        mViewModel.getUC().getLoadingStateEvent().observe(this, new Observer<LoadingStateEnum>() {
            @Override
            public void onChanged(@Nullable LoadingStateEnum stateEnum) {
                onLoadingState(stateEnum);
            }
        });
    }

    public void startActivity(String router, Bundle bundle) {
        ARouter.getInstance().build(router).with(bundle).navigation(this);
    }

    public void onBusEvent(String event, Object object) {

    }

    public void onLoadingState(LoadingStateEnum loadingStateEnum) {

    }
}

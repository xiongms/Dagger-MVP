package com.xiongms.libcore.mvvm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xiongms.libcore.bus.SingleLiveEvent;
import com.xiongms.libcore.enums.LoadingStateEnum;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.HashMap;
import java.util.Map;

public class BaseViewModel extends AndroidViewModel implements LifecycleObserver {

    private UIChangeLiveData uc;
    private LifecycleProvider lifecycle;


    public BaseViewModel(@NonNull Application application) {
        super(application);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {

    }


    /**
     * 注入RxLifecycle生命周期
     *
     * @param lifecycle
     */
    public void injectLifecycleProvider(LifecycleProvider lifecycle) {
        this.lifecycle = lifecycle;
    }

    public LifecycleProvider getLifecycleProvider() {
        return lifecycle;
    }


    /**
     * 跳转页面
     *
     * @param router 所跳转的目的Activity类
     */
    public void startActivity(String router) {
        startActivity(router, null);
    }


    /**
     * 跳转页面
     *
     * @param router 所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(String router, Bundle bundle) {
        Map<String, Object> params = new HashMap();
        params.put(ParameterField.ROUTER, router);
        if (bundle != null) {
            params.put(ParameterField.BUNDLE, bundle);
        }
        uc.getStartActivityEvent().postValue(params);
    }

    public void startActivity(Intent intent) {
        uc.getStartActivityForIntentEvent().postValue(intent);
    }


    public void showDialog() {
        showDialog("请稍后...");
    }

    public void showDialog(String title) {
        uc.getShowDialogEvent().postValue(title);
    }

    public void dismissDialog() {
        uc.getDismissDialogEvent().call();
    }

    public void setLoadingstate(LoadingStateEnum stateEnum) {
        uc.getLoadingStateEvent().setValue(stateEnum);
    }

    /**
     * 关闭界面
     */
    public void finish() {
        uc.getFinishEvent().call();
    }

    /**
     * 返回上一层
     */
    public void onBackPressed() {
        uc.getOnBackPressedEvent().call();
    }

    /**
     * 发送事件消息给页面
     *
     * @param event 事件
     */

    public void sendBusEvent(String event) {
        sendBusEvent(event, null);
    }

    public void sendBusEvent(String event, Object object) {
        Map<String, Object> params = new HashMap();
        params.put(ParameterField.EVENT, event);
        if (object != null) {
            params.put(ParameterField.OBJECT, object);
        }
        uc.getBusEvent().postValue(params);
    }

    public UIChangeLiveData getUC() {
        if (uc == null) {
            uc = new UIChangeLiveData();
        }
        return uc;
    }

    public final class UIChangeLiveData extends SingleLiveEvent {
        private SingleLiveEvent<Map<String, Object>> busEvent;
        private SingleLiveEvent<String> showDialogEvent;
        private SingleLiveEvent<Void> dismissDialogEvent;
        private SingleLiveEvent<Map<String, Object>> startActivityEvent;
        private SingleLiveEvent<Intent> startActivityForIntentEvent;
        private SingleLiveEvent<Map<String, Object>> startContainerActivityEvent;
        private SingleLiveEvent<Void> finishEvent;
        private SingleLiveEvent<Void> onBackPressedEvent;
        private SingleLiveEvent<LoadingStateEnum> loadingState;

        public SingleLiveEvent<LoadingStateEnum> getLoadingStateEvent() {
            return loadingState = createLiveData(loadingState);
        }


        public SingleLiveEvent<Map<String, Object>> getBusEvent() {
            return busEvent = createLiveData(busEvent);
        }

        public SingleLiveEvent<String> getShowDialogEvent() {
            return showDialogEvent = createLiveData(showDialogEvent);
        }

        public SingleLiveEvent<Void> getDismissDialogEvent() {
            return dismissDialogEvent = createLiveData(dismissDialogEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartActivityEvent() {
            return startActivityEvent = createLiveData(startActivityEvent);
        }


        public SingleLiveEvent<Intent> getStartActivityForIntentEvent() {
            return startActivityForIntentEvent = createLiveData(startActivityForIntentEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartContainerActivityEvent() {
            return startContainerActivityEvent = createLiveData(startContainerActivityEvent);
        }

        public SingleLiveEvent<Void> getFinishEvent() {
            return finishEvent = createLiveData(finishEvent);
        }

        public SingleLiveEvent<Void> getOnBackPressedEvent() {
            return onBackPressedEvent = createLiveData(onBackPressedEvent);
        }

        private SingleLiveEvent createLiveData(SingleLiveEvent liveData) {
            if (liveData == null) {
                liveData = new SingleLiveEvent();
            }
            return liveData;
        }

        @Override
        public void observe(LifecycleOwner owner, Observer observer) {
            super.observe(owner, observer);
        }
    }

    public static final class ParameterField {
        public static String ROUTER = "ROUTER";
        public static String BUNDLE = "BUNDLE";
        public static String EVENT = "EVENT";
        public static String OBJECT = "OBJECT";
    }

}

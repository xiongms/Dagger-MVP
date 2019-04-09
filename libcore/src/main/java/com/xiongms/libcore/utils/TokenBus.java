package com.xiongms.libcore.utils;

import android.content.Intent;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.bean.User;
import com.xiongms.libcore.config.RouterConfig;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * 检测
 *
 * @author xiongms
 * @time 2018-08-28 14:23
 */
public class TokenBus {

    private static final String TAG = TokenBus.class.getSimpleName();
    private AtomicBoolean mRefreshing = new AtomicBoolean(false);
    private PublishSubject<User> mPublishSubject;

    public static TokenBus getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final TokenBus INSTANCE = new TokenBus();
    }

    private TokenBus() {

    }

    public Observable<User> getNetTokenLocked() {
        if (mRefreshing.compareAndSet(false, true)) {
            Log.d(TAG, "请求新的Token");
            startLogin();
        } else {
            Log.d(TAG, "Token请求中，等待...");
        }

        if(mPublishSubject == null) {
            mPublishSubject = PublishSubject.create();
        }

        return mPublishSubject;
    }

    /**
     * 跳转到登录页面
     */
    private void startLogin() {
        ARouter.getInstance().build(RouterConfig.ROUTER_LOGIN)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withBoolean("RefreshToken", false)
                .navigation(BaseApplication.getInstance().getContext());
    }

    /**
     * 重新登录已完成，发送消息
     *
     * @param user 新的用户信息
     */
    public void finishToken(User user) {
        if (mPublishSubject != null) {
            mPublishSubject.onNext(user);
            mPublishSubject.onComplete();
        }
        mPublishSubject = null;
        mRefreshing.set(false);
    }

    public void cancelTokenRefresh() {
        if (mPublishSubject != null)
            mPublishSubject.onComplete();
        mPublishSubject = null;
        mRefreshing.set(false);
    }

}

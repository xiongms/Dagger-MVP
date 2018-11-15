package com.xiongms.login.mvp.login;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonObject;
import com.xiongms.libcore.utils.AppPreferencesHelper;
import com.xiongms.login.data.UserServiceApi;
import com.xiongms.libcore.bean.BaseBean;
import com.xiongms.libcore.bean.Store;
import com.xiongms.libcore.bean.User;
import com.xiongms.libcore.config.RouterConfig;
import com.xiongms.libcore.mvp.BasePresenter;
import com.xiongms.libcore.network.rx.RxResultHelper;
import com.xiongms.libcore.network.rx.RxResultSubscriber;
import com.xiongms.libcore.utils.StrUtil;
import com.xiongms.libcore.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 *
 */
public class LoginPresenter extends BasePresenter<Contract.View> implements Contract.Presenter {

    private UserServiceApi userServiceApi;
    private Store mCurrentStoresBean = null;
    private User mUser;

    private String orgMobile;

    private static final int MAX_COUNT_TIME = 60;

    private Observable mObservableCountTime;

    private Observer<Long> mConsumerCountTime;

    private Disposable mDisposable;

    @Inject
    public AppPreferencesHelper mAppPreferencesHelper;

    @Inject
    public LoginPresenter(Retrofit retrofit) {
        userServiceApi = retrofit.create(UserServiceApi.class);
    }

    @Override
    public void initData() {
        orgMobile = mAppPreferencesHelper.getUser().getPhone();

        // 进入登录页面后，删除保存的所有sharedpreference信息
        mAppPreferencesHelper.removeAll();

        mRootView.setPhone(orgMobile);

        mObservableCountTime = Observable.interval(1, TimeUnit.SECONDS, Schedulers.io()).take(MAX_COUNT_TIME) //将递增数字替换成递减的倒计时数字
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return MAX_COUNT_TIME - (aLong + 1);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());


        mConsumerCountTime = new Observer<Long>() {

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
                mRootView.setSendSMSButton(false, true, MAX_COUNT_TIME + "s");
            }

            @Override
            public void onNext(Long aLong) {
                if (aLong == 0) {
                    mRootView.setSendSMSButton(StrUtil.isMobileNo(mRootView.getPhone()) ? true : false, false, "发送验证码");
                    if(mDisposable != null) {
                        mDisposable.dispose();
                        mDisposable = null;
                    }
                } else {
                    mRootView.setSendSMSButton(false, true, aLong + "s");
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    public void login() {

        mRootView.setLoaddingDialogText("登录中...");

        RxResultHelper.getHttpObservable(mRootView.getContext(), Observable.timer(2, TimeUnit.SECONDS))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mRootView.showLoading(true);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        ARouter.getInstance().build(RouterConfig.ROUTER_MAIN).navigation();
                        mRootView.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRootView.hideLoading();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @Override
    public void clickSendSMS() {
        String phone = mRootView.getPhone();

        if (!StrUtil.isMobileNo(phone)) {
            ToastUtil.show("请输入正确的电话号码");
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);

        RxResultHelper.getHttpObservable(mRootView.getContext(), userServiceApi.sendSMS(jsonObject))
                .subscribe(new RxResultSubscriber() {
                    @Override
                    public void start() {
                        mRootView.showLoading(true);
                    }

                    @Override
                    public void error(int code, String msg) {
                        mRootView.hideLoading();
                        ToastUtil.show(msg);
                    }

                    @Override
                    public void success(BaseBean t) {
                        ToastUtil.show("发送成功");
                        startTimer();
                        mRootView.hideLoading();
                    }
                });
    }

    public void startTimer() {
        mObservableCountTime.subscribe(mConsumerCountTime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}

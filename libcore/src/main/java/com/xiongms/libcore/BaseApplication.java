package com.xiongms.libcore;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiongms.libcore.bean.Store;
import com.xiongms.libcore.bean.User;
import com.xiongms.libcore.config.AppConfig;
import com.xiongms.libcore.enums.EventBusTypeEnum;
import com.xiongms.libcore.env.Environment;
import com.xiongms.libcore.utils.LoadViewHelper;
import com.xiongms.libcore.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * @author xiongms
 * @time 2018-08-16 10:38
 */
public abstract class BaseApplication extends Application implements HasActivityInjector {

    private static BaseApplication mApplication;

    public boolean mIsForeground = false;

    public boolean mIsBackToForeground = false;

    @Inject
    public Environment mEnv;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return mApplication;
    }

    public static RefWatcher refWatcher;

    /**
     * 添加多dex包支持
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

        // 安装tinker
        Beta.installTinker();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        initDaggerComponent();
        init();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    public void init() {
        // bugly热修复SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(this, "84a33604f3", true);
        // bugly 异常上报SDK初始化
        CrashReport.initCrashReport(getApplicationContext(), "84a33604f3", true);

        // 部分机型中兼容vector图片
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // 初始化LeakCanary
        refWatcher = LeakCanary.install(this);

        initLogger();
        initArouter();
        initRefreshLayout();
        initLoadingHelper();
        registerActivityLifecycleCallbacks();
        ToastUtil.init(this);
    }

    public Environment getEnv() {
        return mEnv;
    }

    public Store getStore() {
        return mEnv.appPreferencesHelper().getStore();
    }

    public Gson getGson() {
        return mEnv.gson();
    }

    public User getUser() {
        return mEnv.appPreferencesHelper().getUser();
    }

    public Context getContext() {
        return mApplication.getApplicationContext();
    }

    private void initArouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(mApplication);
    }

    private void initRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColors(Color.parseColor("#444444"), Color.WHITE);//全局设置主题颜色
                return new ClassicsHeader(context).setFinishDuration(0).setEnableLastTime(false);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20).setFinishDuration(0);
            }
        });
    }

    /**
     * 初始化加载界面，空界面等
     */
    private void initLoadingHelper() {
        LoadViewHelper.getBuilder()
                .setLoadDefault(R.layout.default_view)
                .setLoadEmpty(R.layout.empty_view)
                .setLoadError(R.layout.error_view)
                .setLoadIng(R.layout.loading_view);
    }

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(AppConfig.LOGGER_TAG)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            private int refCount = 0;

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

                if (refCount == 0) {
                    mIsBackToForeground = true;
                    EventBus.getDefault().post(EventBusTypeEnum.REFRESH_STORE_DATA);
                } else {
                    mIsBackToForeground = false;
                }
                refCount++;
                mIsForeground = true;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                refCount--;
                if (refCount == 0) {
                    mIsForeground = false;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public abstract void initDaggerComponent();
}

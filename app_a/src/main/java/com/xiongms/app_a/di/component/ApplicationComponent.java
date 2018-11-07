package com.xiongms.app_a.di.component;

import com.xiongms.login.di.module.LoginActivitysModule;
import com.xiongms.app_a.di.AppApplication;
import com.xiongms.libcore.di.module.ApplicationModule;
import com.xiongms.libcore.di.module.NetModule;
import com.xiongms.mod_main.di.module.MainActivitysModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * 
 * @author xiongms
 * @time 2018-11-07 16:12
 */
@Singleton
@Component(modules = {
        // dagger2.android相关Module
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        // 公共的Module
        ApplicationModule.class,
        NetModule.class,
        // 当前组件注入module
        LoginActivitysModule.class,
        MainActivitysModule.class})
public interface ApplicationComponent {
    void inject(AppApplication application);
}


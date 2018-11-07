package com.hhg365.login.di.module;

import com.hhg365.login.mvp.login.LoginActivity;
import com.hhg365.login.mvp.login.LoginModule;
import com.xiongms.libcore.di.component.BaseActivityComponent;
import com.xiongms.libcore.di.module.DefaultActivityModule;
import com.xiongms.libcore.di.scope.ActivityScope;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 *
 */
@Module(subcomponents = {
        BaseActivityComponent.class
})
public abstract class LoginActivitysModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = {DefaultActivityModule.class, LoginModule.class})
    abstract LoginActivity contributeLoginActivityInjector();
}

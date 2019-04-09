package com.xiongms.login.di.module;

import com.xiongms.login.mvp.login.LoginActivity;
import com.xiongms.login.mvp.login.LoginModule;
import com.xiongms.libcore.di.component.BaseActivityComponent;
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
    @ContributesAndroidInjector(modules = {LoginModule.class})
    abstract LoginActivity contributeLoginActivityInjector();
}

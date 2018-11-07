package com.xiongms.mod_main.di.module;

import com.xiongms.libcore.di.component.BaseActivityComponent;
import com.xiongms.libcore.di.module.DefaultActivityModule;
import com.xiongms.libcore.di.scope.ActivityScope;
import com.xiongms.mod_main.mvp.main.MainActivity;
import com.xiongms.mod_main.mvp.main.MainModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 *
 */
@Module(subcomponents = {
        BaseActivityComponent.class
})
public abstract class MainActivitysModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = {DefaultActivityModule.class, MainModule.class})
    abstract MainActivity contributeLoginActivityInjector();
}

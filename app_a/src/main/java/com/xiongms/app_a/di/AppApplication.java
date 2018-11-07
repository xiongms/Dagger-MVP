package com.xiongms.app_a.di;

import com.xiongms.app_a.di.component.ApplicationComponent;
import com.xiongms.app_a.di.component.DaggerApplicationComponent;
import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.di.module.ApplicationModule;

/**
 *
 */
public class AppApplication extends BaseApplication {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void initDaggerComponent() {
        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);
    }


    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
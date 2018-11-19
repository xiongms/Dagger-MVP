package com.xiongms.app_a;

import com.xiongms.app_a.di.component.ApplicationComponent;
import com.xiongms.app_a.di.component.DaggerApplicationComponent;
import com.xiongms.app_a.di.network.GlobalHttpHandlerImpl;
import com.xiongms.libcore.BaseApplication;
import com.xiongms.libcore.config.NetConfig;
import com.xiongms.libcore.di.module.ApplicationModule;
import com.xiongms.libcore.di.module.GlobalConfigModule;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> baseUrls = new HashMap<>();
        baseUrls.put("DOMAIN", "http://www.baidu.com/");

        GlobalConfigModule.Builder configModuleBuilder = new GlobalConfigModule.Builder()
                .globalHttpHandler(new GlobalHttpHandlerImpl())
                .baseUrl("http://www.baidu.com/")
                .baseUrls(baseUrls);

        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .globalConfigModule(new GlobalConfigModule(configModuleBuilder))
                .build();

        mApplicationComponent.inject(this);
    }


    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
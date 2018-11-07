package com.xiongms.login.mvp.login;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public abstract class LoginModule {

    @Provides
    @Named("activityName")
    static String provideActivityName() {
        return LoginActivity.class.getSimpleName();
    }

}


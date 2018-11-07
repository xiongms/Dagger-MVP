package com.xiongms.mod_main.mvp.main;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public abstract class MainModule {

    @Provides
    @Named("PresenterClassName")
    static String providePresenterClassName() {
        return MainPresenter.class.getSimpleName();
    }
}


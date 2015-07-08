package com.mtramin.donethat.di.modules;

import android.content.Context;

import com.mtramin.donethat.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by m.ramin on 7/8/15.
 */
@Module
public class ApplicationModule {
    // TODO Activitymodule for activity context

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides @Singleton
    Context provideApplicationContext() {
        return application;
    }
}

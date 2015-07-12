package com.mtramin.donethat.di.modules;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by m.ramin on 7/12/15.
 */
@Module
public class UtilModule {

    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }
}

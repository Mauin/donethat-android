package com.mtramin.donethat.di.modules;

import com.mtramin.donethat.api.DonethatApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by m.ramin on 7/5/15.
 */
@Module
public class ServiceModule {

    @Provides
    public DonethatApiService provideDonethatApiService() {
        return new DonethatApiService();
    }
}

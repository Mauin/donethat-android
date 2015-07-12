package com.mtramin.donethat.di.modules;

import android.content.Context;

import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.api.TwitterAuthService;

import javax.inject.Inject;
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

    @Provides @Inject
    public TwitterAuthService provideTwitterAuthService(Context context) {
        return new TwitterAuthService(context);
    }
}

package com.mtramin.donethat.di.modules;

import android.content.Context;

import com.mtramin.donethat.api.SyncService;
import com.mtramin.donethat.api.TwitterAuthService;
import com.mtramin.donethat.data.persist.DonethatCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by m.ramin on 7/5/15.
 */
@Module
public class ServiceModule {

    @Provides
    @Singleton
    public SyncService provideSyncService(Context context) {
        return new SyncService(context);
    }

    @Provides
    @Singleton
    public DonethatCache provideDonethatCache() {
        return new DonethatCache();
    }

    @Provides
    @Singleton
    public TwitterAuthService provideTwitterAuthService(Context context) {
        return new TwitterAuthService(context);
    }
}

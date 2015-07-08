package com.mtramin.donethat.di.modules;

import android.content.Context;

import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.auth.TwitterAuthenticationService;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.unicate.retroauth.AuthRestAdapter;
import eu.unicate.retroauth.interceptors.TokenInterceptor;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by m.ramin on 7/5/15.
 */
@Module
public class ApiModule {

    private static final String ENDPOINT_DONETHAT = "https://donethat-test.herokuapp.com";

    @Provides @Inject
    DonethatApi provideDonethatApi(Context context) {
        AuthRestAdapter restAdapter = new AuthRestAdapter.Builder()
                .setEndpoint(ENDPOINT_DONETHAT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter.create(context, new SomeTokenInterceptor(), DonethatApi.class);
    }

    public class SomeTokenInterceptor extends TokenInterceptor {
        @Override
        public void injectToken(RequestInterceptor.RequestFacade facade, String token) {
            facade.addHeader("Authorization", "Bearer " + token);
        }
    }

}

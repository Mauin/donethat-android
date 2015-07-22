package com.mtramin.donethat.di.modules;

import android.accounts.Account;
import android.content.Context;

import com.mtramin.donethat.api.interfaces.DonethatApi;
import com.mtramin.donethat.util.AccountUtil;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import eu.unicate.retroauth.AuthRestAdapter;
import eu.unicate.retroauth.interceptors.TokenInterceptor;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.LoganSquareConverter;

/**
 * Created by m.ramin on 7/5/15.
 */
@Module
public class ApiModule {

    private static final String ENDPOINT_DONETHAT = "https://donethat-test.herokuapp.com";
    private final Context context;

    public ApiModule(Context context) {
        this.context = context;
    }

    @Provides
    @Inject
    DonethatApi provideDonethatApi(Context context) {
        AuthRestAdapter restAdapter = new AuthRestAdapter.Builder()
                .setConverter(new LoganSquareConverter())
                .setEndpoint(ENDPOINT_DONETHAT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter.create(context, new DonethatTokenInterceptor(), DonethatApi.class);
    }

    private class DonethatTokenInterceptor extends TokenInterceptor {
        @Override
        public void injectToken(RequestFacade requestFacade, String token) {
            String userId = AccountUtil.getUserData(context, "USER_ID");
            String xAuthToken = String.format("%s:%s", userId, token);

            requestFacade.addHeader("X-Auth-Token", xAuthToken);
        }
    }
}

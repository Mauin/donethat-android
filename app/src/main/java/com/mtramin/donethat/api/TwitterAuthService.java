package com.mtramin.donethat.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.mtramin.donethat.Application;
import com.mtramin.donethat.BuildConfig;
import com.mtramin.donethat.auth.TwitterAuthenticationService;
import com.mtramin.donethat.data.twitter.TwitterUser;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.StreamUtils;

import java.util.Scanner;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by m.ramin on 7/11/15.
 */
public class TwitterAuthService {

    private static final String USER_DATA_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";

    private OAuthService service;
    private Token requestToken;

    @Inject
    Gson gson;

    public TwitterAuthService(Context context) {
        ((Application) context.getApplicationContext()).getComponent().inject(this);

        // If you choose to use a callback, "oauth_verifier" will be the return value by Twitter (request param)
        service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(BuildConfig.donethat_twitter_api_key)
                .apiSecret(BuildConfig.donethat_twitter_api_secret)
                .callback("oauth://twitter")
                .build();
    }

    public Observable<String> getAuthUrl() {
        return requestToken()
                .map(service::getAuthorizationUrl);
    }

    public Observable<Token> requestToken() {
        return Observable.create(subscriber -> {
            requestToken = service.getRequestToken();
            subscriber.onNext(requestToken);
            subscriber.onCompleted();
        });
    }

    public Observable<Token> accessToken(Verifier verifier) {
        return Observable.create(subscriber -> {
            Token accessToken = service.getAccessToken(requestToken, verifier);
            subscriber.onNext(accessToken);
            subscriber.onCompleted();
        });
    }

    public Observable<TwitterUser> getUserData(Token token) {
        return Observable.create(subscriber -> {
            OAuthRequest request = new OAuthRequest(Verb.GET, USER_DATA_URL);
            service.signRequest(token, request);
            Response response = request.send();

            String jsonData = StreamUtils.getStreamContents(response.getStream());
            TwitterUser twitterUser = gson.fromJson(jsonData, TwitterUser.class);

            subscriber.onNext(twitterUser);
            subscriber.onCompleted();
        });


    }
}

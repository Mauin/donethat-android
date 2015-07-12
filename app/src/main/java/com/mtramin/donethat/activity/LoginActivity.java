package com.mtramin.donethat.activity;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.api.TwitterAuthService;
import com.mtramin.donethat.data.twitter.TwitterUser;
import com.mtramin.donethat.util.LogUtil;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.unicate.retroauth.AuthenticationActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/8/15.
 */
public class LoginActivity extends AuthenticationActivity {

    @Bind(R.id.twitter_login_button)
    Button loginButton;

    @Bind(R.id.webview)
    WebView webView;

    @Bind(R.id.login_content)
    View content;

    @Inject
    TwitterAuthService twitterAuthService;

    CompositeSubscription subscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Application) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        subscription.unsubscribe();
    }

    @OnClick(R.id.twitter_login_button)
    public void loginWithTwitter() {

        subscription.add(twitterAuthService.getAuthUrl()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(authUrl -> {
                                    webView.setWebViewClient(new TwitterWebViewClient());
                                    webView.loadUrl(authUrl);
                                    webView.setVisibility(View.VISIBLE);
                                    content.setVisibility(View.GONE);
                                }, throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }

    private void extractVerifier(String url) {
        Uri uri = Uri.parse(url);
        String oauthVerifier = uri.getQueryParameter("oauth_verifier");
        Verifier verifier = new Verifier(oauthVerifier);

        requestAccessToken(verifier);
    }

    private void requestAccessToken(Verifier verifier) {
        subscription.add(twitterAuthService.accessToken(verifier)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::getUserData, throwable -> LogUtil.logException(this, throwable)
                        )
        );
    }

    private void getUserData(Token token) {
        subscription.add(twitterAuthService.getUserData(token)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(twitterUser -> {
                            saveUserData(token, twitterUser);
                        }, throwable -> LogUtil.logException(this, throwable))
        );
    }

    private void saveUserData(Token token, TwitterUser twitterUser) {
        Bundle userData = new Bundle();
        userData.putString("NAME", twitterUser.name);
        userData.putString("SCREEN_NAME", twitterUser.screen_name);
        userData.putString("DESCRIPTION", twitterUser.description);
        userData.putString("BACKGOROUND_IMAGE_URL", twitterUser.profile_background_image_url_https);
        userData.putString("PROFILE_IMAGE_URL", twitterUser.profile_image_url_https);

        finalizeAuthentication(twitterUser.screen_name, getString(R.string.auth_token_type), token.getToken(), userData);

        startActivity(TripsActivity.createIntent(this));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, context.getString(R.string.auth_account_type));
        return intent;
    }

    private class TwitterWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            if (url.startsWith("oauth")) {
                extractVerifier(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(wv, url);
        }
    }
}

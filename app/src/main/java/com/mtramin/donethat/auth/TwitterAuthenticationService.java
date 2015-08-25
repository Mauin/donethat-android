package com.mtramin.donethat.auth;

import android.content.Context;

import com.mtramin.donethat.R;

import eu.unicate.retroauth.AuthenticationService;

/**
 * Created by m.ramin on 7/8/15.
 */
public class TwitterAuthenticationService extends AuthenticationService {
    public static final String ACCOUNT_USERNAME = "NAME";
    public static final String ACCOUNT_SCREEN_NAME = "SCREEN_NAME";
    public static final String ACCOUNT_DESCRIPTION = "DESCRIPTION";
    public static final String ACCOUNT_BACKGOROUND_IMAGE_URL = "BACKGOROUND_IMAGE_URL";
    public static final String ACCOUNT_PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";
    public static final String ACCOUNT_USER_ID = "USER_ID";

    @Override
    public String getLoginAction(Context context) {
        // this is used only to provide the action for the LoginActivity to open
        return context.getString(R.string.authentication_action);
    }
}

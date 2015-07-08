package com.mtramin.donethat.auth;

import android.content.Context;

import com.mtramin.donethat.R;

import eu.unicate.retroauth.AuthenticationService;

/**
 * Created by m.ramin on 7/8/15.
 */
public class TwitterAuthenticationService extends AuthenticationService {
    @Override
    public String getLoginAction(Context context) {
        // this is used only to provide the action for the LoginActivity to open
        return context.getString(R.string.authentication_action);
    }
}

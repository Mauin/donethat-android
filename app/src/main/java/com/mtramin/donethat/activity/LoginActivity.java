package com.mtramin.donethat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mtramin.donethat.R;

import eu.unicate.retroauth.AuthenticationActivity;

/**
 * Created by m.ramin on 7/8/15.
 */
public class LoginActivity extends AuthenticationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

}

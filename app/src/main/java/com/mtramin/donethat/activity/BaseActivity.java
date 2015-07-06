package com.mtramin.donethat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mtramin.donethat.Application;

import butterknife.ButterKnife;

/**
 * Created by m.ramin on 7/5/15.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);
    }
}

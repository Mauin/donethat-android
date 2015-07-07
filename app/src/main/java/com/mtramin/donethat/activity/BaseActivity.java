package com.mtramin.donethat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by m.ramin on 7/5/15.
 */
public class BaseActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
    }
}

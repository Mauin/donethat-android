package com.mtramin.donethat.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;

import com.mtramin.donethat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by m.ramin on 7/5/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setActivityStyle(Palette palette, CollapsingToolbarLayout toolbarLayout) {
        toolbarLayout.setBackgroundColor(palette.getMutedColor(ContextCompat.getColor(this, R.color.primary)));
        toolbarLayout.setCollapsedTitleTextColor(palette.getVibrantColor(ContextCompat.getColor(this, android.R.color.white)));
        toolbarLayout.setContentScrimColor(palette.getMutedColor(ContextCompat.getColor(this, R.color.primary)));
        toolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(ContextCompat.getColor(this, R.color.primary_dark)));
    }
}

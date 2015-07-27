package com.mtramin.donethat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.ui.tripdetails.TripDetailsFragment;
import com.mtramin.donethat.util.AccountUtil;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements FragmentCallbacks {

    @Bind(R.id.content)
    FrameLayout content;

    @Bind(R.id.navigation_view)
    NavigationView navView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Application) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_main);
        setupNavigationView(navView);

        if (!AccountUtil.hasAccount(this)) {
            startActivity(LoginActivity.createIntent(this));
            finish();
        }

        TripsFragment fragment = TripsFragment.newInstance();
        showFragment(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupNavigationView(NavigationView view) {
        view.setNavigationItemSelectedListener(menuItem -> {
            drawer.closeDrawers();
            return true;
        });
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public void onShowTripDetails(Trip trip) {
        Fragment fragment = TripDetailsFragment.newInstance(trip);

        showFragment(fragment);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment).commit();
    }
}

package com.mtramin.donethat.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.auth.TwitterAuthenticationService;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.model.twitter.TwitterUser;
import com.mtramin.donethat.databinding.ActivityMainBinding;
import com.mtramin.donethat.databinding.DrawerHeaderBinding;
import com.mtramin.donethat.ui.tripdetails.TripDetailFragment;
import com.mtramin.donethat.util.AccountUtil;

import java.util.UUID;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements FragmentCallbacks {

    public static final String TAG_FRAGMENT_TRIPS = "TRIPS";
    public static final String TAG_FRAGMENT_TRIP_DETAILS = "TRIPS_DETAILS";

    public static final String EXTRA_FRAGMENT_TAG = "FRAGMENT_TAG";

    private ActivityMainBinding binding;
    private DrawerHeaderBinding drawerHeaderBinding;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Application) getApplication()).getComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupNavigationView(binding.navigationView);

        if (!AccountUtil.hasAccount(this)) {
            startActivity(LoginActivity.createIntent(this));
            finish();
        }

        setUpFragment();
    }

    private void setUpFragment() {
        Intent intent = getIntent();

        if (intent == null) {
            showFragment(TripsFragment.newInstance(), false);
            return;
        }

        String fragmentTag = TAG_FRAGMENT_TRIPS;
        String intentFragmentTag = intent.getStringExtra(EXTRA_FRAGMENT_TAG);
        if (!TextUtils.isEmpty(intentFragmentTag)) {
            fragmentTag = intentFragmentTag;
        }

        switch (fragmentTag) {
            case TAG_FRAGMENT_TRIPS:
                showFragment(TripsFragment.newInstance(), false);
                break;
            case TAG_FRAGMENT_TRIP_DETAILS:
                UUID tripId = (UUID) intent.getSerializableExtra(TripDetailFragment.EXTRA_TRIP_ID);
                showFragment(TripDetailFragment.newInstance(tripId), false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.content);

            if (currentFragment instanceof TripDetailFragment) {
                showFragment(TripsFragment.newInstance(), false);
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, 0, 0);
        binding.drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupNavigationView(NavigationView view) {
        view.setNavigationItemSelectedListener(menuItem -> {
            binding.drawerLayout.closeDrawers();
            return true;
        });

        drawerHeaderBinding = DataBindingUtil.bind(view.inflateHeaderView(R.layout.drawer_header));

        if (AccountUtil.hasAccount(this)) {
            TwitterUser user = AccountUtil.getUser(this);
            drawerHeaderBinding.setUser(user);
        }
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public void onShowTripDetails(Trip trip) {
        Fragment fragment = TripDetailFragment.newInstance(trip.id);
        showFragment(fragment, true);
    }

    private void showFragment(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (addToBackstack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, fragment)
                .commit();
    }
}

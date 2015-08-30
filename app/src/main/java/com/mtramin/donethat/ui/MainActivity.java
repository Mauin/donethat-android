package com.mtramin.donethat.ui;

import android.content.Context;
import android.content.Intent;
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
import com.mtramin.donethat.ui.tripdetails.TripDetailFragment;
import com.mtramin.donethat.util.AccountUtil;

import java.util.UUID;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements FragmentCallbacks {

    public static final String TAG_FRAGMENT_TRIPS = "TRIPS";
    public static final String TAG_FRAGMENT_TRIP_DETAILS = "TRIPS_DETAILS";

    public static final String EXTRA_FRAGMENT_TAG = "FRAGMENT_TAG";

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

        if (AccountUtil.hasAccount(this)) {
            ImageView background = (ImageView) view.findViewById(R.id.header_background);
            ImageView avatar = (ImageView) view.findViewById(R.id.header_avatar);
            TextView userName = (TextView) view.findViewById(R.id.header_username);
            TextView userHandle = (TextView) view.findViewById(R.id.header_userdetail);

            String accountName = AccountUtil.getUserData(this, TwitterAuthenticationService.ACCOUNT_USERNAME);
            String accountHandle = "@" + AccountUtil.getUserData(this, TwitterAuthenticationService.ACCOUNT_SCREEN_NAME);
            String backgroundUrl = AccountUtil.getUserData(this, TwitterAuthenticationService.ACCOUNT_BACKGOROUND_IMAGE_URL);
            String avatarUrl = AccountUtil.getUserData(this, TwitterAuthenticationService.ACCOUNT_PROFILE_IMAGE_URL);

            userName.setText(accountName);
            userHandle.setText(accountHandle);

            Glide.with(this)
                    .load(backgroundUrl)
                    .asBitmap()
                    .into(background);

            Glide.with(this)
                    .load(avatarUrl)
                    .asBitmap()
                    .into(avatar);
        }
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public void onShowTripDetails(Trip trip) {
        Fragment fragment = TripDetailFragment.newInstance(trip.id);

        // TODO fragment transition on the trip name
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

package com.mtramin.donethat.ui;

import android.Manifest;
import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.api.DonethatApiService;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;
import com.mtramin.donethat.databinding.ActivityNoteEditBinding;
import com.mtramin.donethat.observable.googleApiClient.ObservableLastLocation;
import com.mtramin.donethat.service.BackgroundSyncService;
import com.mtramin.donethat.util.IntentUtils;
import com.mtramin.donethat.util.LogUtil;
import com.mtramin.donethat.util.PermissionUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.text.DateFormat;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.internal.MapFactory;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by m.ramin on 7/8/15.
 */
public class EditNoteActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String EXTRA_TRIP = "extra_trip";
    public static final String EXTRA_NOTE = "extra_note";
    private static final int REQUEST_CODE_LOCATION = 100;

    private ActivityNoteEditBinding binding;

    @Inject
    DonethatCache storage;

    private CompositeSubscription subscription;

    private Trip trip;
    private Note note;

    private LatLng location;
    private DateTime date;

    @Inject
    DonethatApiService api;
    private Snackbar permissionSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Application) getApplication()).getComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_edit);
        ButterKnife.bind(this);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        parseIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscription = new CompositeSubscription();

        if (this.note != null) {
            enterNoteContent(note);
        } else {
            setToday();
            if (PermissionUtil.shouldRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    this.permissionSnackbar = Snackbar.make(binding.getRoot(), "For the best experience, you can store the location of your notes! We need this permission to determine where we should place this note!", Snackbar.LENGTH_INDEFINITE);
                    this.permissionSnackbar.show();
                }

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionUtil.REQUEST_CODE_ACCESS_FINE_LOCATION);
                return;
            }
            subscribeToCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (this.permissionSnackbar != null) {
            this.permissionSnackbar.dismiss();
        }

        if (permissions.length == 0) {
            // No permissions granted/denied
            return;
        }

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_ACCESS_FINE_LOCATION: {
                if (PermissionUtil.permissionGranted(grantResults)) {
                    subscribeToCurrentLocation();
                } else {
                    binding.editNoteLocation.setText("Access to location provider denied. Select manually...");
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setToday() {
        binding.editNoteDate.setText(DateTime.now().toString());
    }

    private void subscribeToCurrentLocation() {
        subscription.add(ObservableLastLocation.create(this)
                .subscribe(this::setCurrentLocation, throwable -> LogUtil.logException(this, throwable)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void parseIntent() {
        Intent intent = getIntent();
        UUID tripId = (UUID) intent.getSerializableExtra(EXTRA_TRIP);
        this.trip = storage.getTripDetails(tripId);

        if (intent.hasExtra(EXTRA_NOTE)) {
            UUID noteId = (UUID) intent.getSerializableExtra(EXTRA_NOTE);
            this.note = storage.getNote(noteId);
        }
    }

    public static Intent createIntent(Context context, UUID tripId) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(EXTRA_TRIP, tripId);
        return intent;
    }

    public static Intent createIntent(Context context, UUID tripId, UUID noteId) {
        Intent intent = createIntent(context, tripId);
        intent.putExtra(EXTRA_NOTE, noteId);
        return intent;
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        String title = binding.editNoteTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            binding.editNoteTitle.setError("Please enter at least a title for your note.");
            return;
        }

        if (this.note != null) {
            note.title = title;
            note.content = binding.editNoteContent.getText().toString();
            note.location = location;
            note.date = date;
            storeNote(note);
            return;
        }

        Note note = new Note(title, binding.editNoteContent.getText().toString(), trip.id);
        storeNote(note);
    }

    @OnClick(R.id.edit_note_date)
    public void onDateClicked() {
        showDatePicker();
    }

    @OnClick(R.id.edit_note_location)
    public void onLocationClicked() {
        startActivityForResult(MapActivity.createIntent(this), REQUEST_CODE_LOCATION);
    }

    private void setCurrentLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setCurrentLocation(latLng);
    }

    private void setCurrentLocation(LatLng location) {
        if (location == null) {
            return;
        }

        // TODO Geocoder with fallback latlng String
        binding.editNoteLocation.setText(location.toString());
    }

    private void storeNote(Note note) {
        storage.storeNote(trip.id, note);
        startService(BackgroundSyncService.tripSyncIntent(this, trip.id));
        finish();
    }

    public void enterNoteContent(Note note) {
        binding.setNote(note);
        loadImage(note.image);

        binding.editNoteImage.setOnClickListener(v -> {
            IntentUtils.launchImagePicker(this);
        });

        setCurrentLocation(note.location);
    }

    private void loadImage(Uri imageUri) {
        if (imageUri == null) {
            return;
        }

        Glide.with(this)
                .load(imageUri)
                .asBitmap()
                .placeholder(R.color.primary)
                .into(new BitmapImageViewTarget(binding.editNoteImage) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        Animator reveal = ViewAnimationUtils.createCircularReveal(binding.editNoteImage, binding.editNoteImage.getWidth() / 2, binding.editNoteImage.getHeight() / 2, 0, binding.editNoteImage.getWidth() / 2);
                        reveal.setDuration(300);
                        reveal.setInterpolator(new DecelerateInterpolator());
                        reveal.start();
                        Palette.from(resource).generate(palette -> setActivityStyle(palette, binding.toolbarCollapsing));
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case IntentUtils.REQUEST_CODE_PICK_IMAGE:
                note.image = data.getData();
                loadImage(note.image);
                break;

            case REQUEST_CODE_LOCATION:
                note.location = new LatLng(data.getDoubleExtra(MapActivity.EXTRA_LATITUDE, 0.0), data.getDoubleExtra(MapActivity.EXTRA_LONGITUDE, 0.0));
                binding.editNoteLocation.setText(note.location.toString());
                break;
            default:
                throw new IllegalStateException("Unknown activity request code");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDatePicker() {
        if (this.date == null) {
            this.date = DateTime.now();
        }
        DatePickerDialog datePicker = new DatePickerDialog(this, this, date.year().get(), date.monthOfYear().get(), date.dayOfMonth().get());
        datePicker.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.date = this.date.year().setCopy(year);
        this.date = this.date.monthOfYear().setCopy(monthOfYear);
        this.date = this.date.dayOfMonth().setCopy(dayOfMonth);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, this.date.hourOfDay().get(), this.date.minuteOfHour().get(), true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.date = this.date.hourOfDay().setCopy(hourOfDay);
        this.date = this.date.minuteOfHour().setCopy(minute);

        note.date = this.date;
        binding.editNoteDate.setText(this.date.toString());
    }
}

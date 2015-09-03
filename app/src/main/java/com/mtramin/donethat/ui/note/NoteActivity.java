package com.mtramin.donethat.ui.note;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mtramin.donethat.Application;
import com.mtramin.donethat.R;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;
import com.mtramin.donethat.ui.BaseActivity;
import com.mtramin.donethat.ui.EditNoteActivity;
import com.mtramin.donethat.ui.MainActivity;
import com.mtramin.donethat.ui.tripdetails.TripDetailFragment;
import com.mtramin.donethat.util.ViewUtil;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by m.ramin on 7/7/15.
 */
public class NoteActivity extends BaseActivity {

    private static final String EXTRA_NOTE_ID = "EXTRA_NOTE_ID";
    private static final String EXTRA_TRIP_ID = "EXTRA_TRIP_ID";
    private static final String EXTRA_PALETTE = "EXTRA_PALETTE";

    private UUID noteId;
    private Note note;
    private Trip trip;

    @Inject
    DonethatCache storage;

    @Bind(R.id.note_title)
    TextView title;

    @Bind(R.id.note_content)
    TextView content;

    @Bind(R.id.note_date)
    TextView date;

    @Bind(R.id.note_location)
    TextView location;

    @Bind(R.id.note_image)
    ImageView image;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_collapsing)
    CollapsingToolbarLayout collapsingToolbar;

    @Bind(R.id.root)
    ViewGroup root;

    @Bind(R.id.appbar)
    ViewGroup appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        ((Application) getApplication()).getComponent().inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean create = super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_note, menu);

        return create;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_note_edit:
                startActivity(EditNoteActivity.createIntent(this, trip.id, note.id));
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayNote() {
        title.setText(note.title);
        content.setText(note.content);
        date.setText(DateUtils.formatSameDayTime(note.date.getMillis(), DateTime.now().getMillis(), DateFormat.DEFAULT, DateFormat.DEFAULT));

        if (note.location != null) {
            location.setText(note.location.toString());
        }

        if (note.image == null) {
            image.setVisibility(View.GONE);
            // Have to set padding for toolbar, otherwise it will be cut off
            collapsingToolbar.setPadding(0, ViewUtil.getStatusBarHeight(this), 0, 0);
        } else {
            Glide.with(this)
                    .load(note.image)
                    .asBitmap()
                    .placeholder(R.color.primary)
                    .into(new BitmapImageViewTarget(image) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            super.onResourceReady(resource, glideAnimation);
                            Animator reveal = ViewAnimationUtils.createCircularReveal(image, image.getWidth()/2, image.getHeight()/2, 0, image.getWidth()/2);
                            reveal.setDuration(300);
                            reveal.setInterpolator(new DecelerateInterpolator());
                            reveal.start();
                            Palette.from(resource).generate(palette -> setActivityStyle(palette, collapsingToolbar));
                        }
                    });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.note = storage.getNote(noteId);
        displayNote();
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        this.noteId = (UUID) intent.getSerializableExtra(EXTRA_NOTE_ID);
        UUID tripId = (UUID) intent.getSerializableExtra(EXTRA_TRIP_ID);
        this.trip = storage.getTripDetails(tripId);
    }

    public static Intent createIntent(Context context, UUID noteId, UUID tripId) {
        // TODO pass palette to this for even more color amazingness
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        intent.putExtra(EXTRA_TRIP_ID, tripId);
        return intent;
    }
}

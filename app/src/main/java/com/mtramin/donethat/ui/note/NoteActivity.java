package com.mtramin.donethat.ui.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mtramin.donethat.R;
import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.Trip;
import com.mtramin.donethat.ui.BaseActivity;
import com.mtramin.donethat.ui.EditNoteActivity;

import org.joda.time.DateTime;

import java.text.DateFormat;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by m.ramin on 7/7/15.
 */
public class NoteActivity extends BaseActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";
    private static final String EXTRA_TRIP = "EXTRA_TRIP";

    private Note note;
    private Trip trip;

    @Bind(R.id.note_title)
    TextView title;

    @Bind(R.id.note_content)
    TextView content;

    @Bind(R.id.note_date)
    TextView date;

    @Bind(R.id.note_image)
    ImageView image;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        handleIntent(getIntent());
        displayNote();
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View v) {
        startActivity(EditNoteActivity.createIntent(this, trip));
    }

    private void displayNote() {
        title.setText(note.title);
        content.setText(note.content);
        date.setText(DateUtils.formatSameDayTime(note.date.getMillis(), DateTime.now().getMillis(), DateFormat.DEFAULT, DateFormat.DEFAULT));

        if (TextUtils.isEmpty(note.image.toString())) {
            image.setVisibility(View.GONE);
        } else {
            Glide.with(this)
                    .load(note.image)
                    .asBitmap()
                    .into(new BitmapImageViewTarget(image) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            super.onResourceReady(resource, glideAnimation);

                            Palette.from(resource).generate(palette -> {
                                toolbar.setBackgroundColor(palette.getVibrantColor(R.color.primary));
                            });
                        }
                    });
        }

    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        this.note = intent.getParcelableExtra(EXTRA_NOTE);
    }

    public static Intent createIntent(Context context, Note note, Trip trip) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        intent.putExtra(EXTRA_TRIP, trip);
        return intent;
    }

}

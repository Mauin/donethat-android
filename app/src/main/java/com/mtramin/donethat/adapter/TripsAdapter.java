package com.mtramin.donethat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by m.ramin on 7/5/15.
 */
public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripViewHolder> {

    @Inject
    DonethatCache database;

    Context context;

    List<Trip> data;
    Map<Trip, Uri> tripImages = new HashMap<>();

    PublishSubject<Trip> observableTripSelection = PublishSubject.create();

    public TripsAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();

        ((Application) context.getApplicationContext()).getComponent().inject(this);
    }

    public void setData(List<Trip> data) {
        this.data.clear();
        this.data.addAll(data);

        for (Trip trip : this.data) {
            List<Note> notes = database.getNotesForTrip(trip.id);
            for (Note note : notes) {
                if (note.image != null) {
                    tripImages.put(trip, note.image);
                    break;
                }
            }
        }

        notifyItemRangeInserted(0, data.size() - 1);
    }

    public void addData(List<Trip> data) {
        for (Trip trip : this.data) {
            if (data.contains(trip)) {
                this.data.remove(trip);
            }
        }

        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public Observable<Trip> onTripClick() {
        return observableTripSelection;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View holder = inflater.inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(holder);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Trip trip = data.get(position);

        holder.title.setText(trip.title);
        holder.created.setText(trip.updated.toString());
        if (tripImages.containsKey(trip)) {
            holder.image.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(tripImages.get(trip))
                    .asBitmap()
                    .placeholder(R.color.primary)
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }

        holder.item.setOnClickListener(v -> observableTripSelection.onNext(trip));
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.trip_item)
        View item;
        @Bind(R.id.trip_title)
        TextView title;
        @Bind(R.id.trip_created_at)
        TextView created;
        @Bind(R.id.trip_image)
        ImageView image;
        @Bind(R.id.trip_divider)
        View divider;

        public TripViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

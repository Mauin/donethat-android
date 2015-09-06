package com.mtramin.donethat.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtramin.donethat.Application;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.DonethatCache;
import com.mtramin.donethat.databinding.ItemTripBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripViewHolder> {

    private static final String TAG = TripsAdapter.class.getName();
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
        insertData(data);

        if (this.data != null && this.data.size() > 0) {
            notifyDataSetChanged();
            return;
        }

        notifyItemRangeInserted(0, getItemCount() - 1);
    }

    private void insertData(List<Trip> data) {
        tripImages.clear();
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
        ItemTripBinding binding = ItemTripBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TripViewHolder(binding.getRoot());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Trip trip = data.get(position);

        holder.binding.setTrip(trip);
        holder.binding.setImage(tripImages.get(trip));

        holder.binding.getRoot().setOnClickListener(v -> observableTripSelection.onNext(trip));
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        ItemTripBinding binding;

        public TripViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}

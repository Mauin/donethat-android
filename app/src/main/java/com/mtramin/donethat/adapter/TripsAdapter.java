package com.mtramin.donethat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtramin.donethat.R;
import com.mtramin.donethat.data.Trip;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by m.ramin on 7/5/15.
 */
public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripViewHolder> {

    List<Trip> data;

    PublishSubject<Trip> observableTripSelection = PublishSubject.create();

    public TripsAdapter() {
        this.data = new ArrayList<>();
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
//        holder.created.setText(trip.date.toString());

        holder.item.setOnClickListener(v -> observableTripSelection.onNext(trip));
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.trip_item)
        View item;
        @Bind(R.id.trip_title)
        TextView title;
        @Bind(R.id.trip_created_at)
        TextView created;

        public TripViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

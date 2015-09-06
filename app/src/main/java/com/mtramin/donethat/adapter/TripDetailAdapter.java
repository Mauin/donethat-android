package com.mtramin.donethat.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.databinding.ItemTripDetailsHeaderBinding;
import com.mtramin.donethat.databinding.ItemTripDetailsNoteBinding;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by m.ramin on 7/5/15.
 */
public class TripDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Trip data;
    List<Note> notes = new ArrayList<>();

    PublishSubject<Note> observableNoteSelection = PublishSubject.create();

    Context context;

    public TripDetailAdapter(Context context) {
        this.context = context;
    }

    public void setData(Trip data, List<Note> notes) {
        this.data = data;

        this.notes.clear();
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    public Observable<Note> onNoteClicked() {
        return observableNoteSelection;
    }

    private Note getItem(int position) {
        return notes.get(position - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (ITEM_TYPE.values()[viewType]) {
            case ITEM_HEADER:
                ItemTripDetailsHeaderBinding headerBinding = ItemTripDetailsHeaderBinding.inflate(inflater, parent, false);
                return new HeaderViewHolder(headerBinding.getRoot());
            case ITEM_NOTE:
                ItemTripDetailsNoteBinding noteBinding = ItemTripDetailsNoteBinding.inflate(inflater, parent, false);
                return new NoteViewHolder(noteBinding.getRoot());
            default:
                throw new IllegalStateException("undefined item type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.ITEM_HEADER.ordinal();
        }
        return ITEM_TYPE.ITEM_NOTE.ordinal();
    }

    private enum ITEM_TYPE {
        ITEM_HEADER, ITEM_NOTE
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }

        if (notes == null) {
            return 1;
        }

        return notes.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ITEM_TYPE itemType = ITEM_TYPE.values()[getItemViewType(position)];
        switch (itemType) {
            case ITEM_HEADER:
                ((HeaderViewHolder) holder).binding.setTrip(data);
                break;
            case ITEM_NOTE:
                Note item = getItem(position);
                ((NoteViewHolder) holder).binding.setNote(item);
                ((NoteViewHolder) holder).binding.getRoot().setOnClickListener(v -> observableNoteSelection.onNext(item));
                break;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        ItemTripDetailsHeaderBinding binding;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        ItemTripDetailsNoteBinding binding;

        public NoteViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}

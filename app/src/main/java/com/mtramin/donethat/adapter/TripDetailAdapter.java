package com.mtramin.donethat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtramin.donethat.R;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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

        View holder = null;
        switch (ITEM_TYPE.values()[viewType]) {
            case ITEM_HEADER:
                holder = inflater.inflate(R.layout.item_trip_details_header, parent, false);
                return new HeaderViewHolder(holder);
            case ITEM_NOTE:
                holder = inflater.inflate(R.layout.item_trip_details_note, parent, false);
                return new NoteViewHolder(holder);
            default:
                throw new IllegalStateException("undefined type");
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
                ((HeaderViewHolder) holder).title.setText(data.title);
                ((HeaderViewHolder) holder).description.setText(data.content);
                break;
            case ITEM_NOTE:
                Note note = getItem(position);

                String created = DateUtils.formatDateTime(context, note.date.getMillis(), DateUtils.FORMAT_SHOW_DATE);
                ((NoteViewHolder) holder).created.setText(created);

                ((NoteViewHolder) holder).title.setText(note.title);
                ((NoteViewHolder) holder).content.setText(note.content);

                if (note.image == null) {
                    ((NoteViewHolder) holder).image.setVisibility(View.GONE);
                    ((NoteViewHolder) holder).divider.setVisibility(View.GONE);
                } else {
                    ((NoteViewHolder) holder).divider.setVisibility(View.VISIBLE);
                    ((NoteViewHolder) holder).image.setVisibility(View.VISIBLE);
                    Glide.with(((NoteViewHolder) holder).image.getContext())
                            .load(note.image)
                            .asBitmap()
                            .into(((NoteViewHolder) holder).image);
                }

                ((NoteViewHolder) holder).item.setOnClickListener(v -> observableNoteSelection.onNext(note));
                break;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.trip_deatil_title)
        TextView title;

        @Bind(R.id.trip_deatil_description)
        TextView description;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.note_item)
        View item;
        @Bind(R.id.note_title)
        TextView title;
        @Bind(R.id.note_content)
        TextView content;
        @Bind(R.id.note_date)
        TextView created;
        @Bind(R.id.note_image)
        ImageView image;
        @Bind(R.id.note_divider)
        View divider;

        public NoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

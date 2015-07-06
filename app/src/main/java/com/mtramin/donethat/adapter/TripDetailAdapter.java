package com.mtramin.donethat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtramin.donethat.R;
import com.mtramin.donethat.data.Note;
import com.mtramin.donethat.data.TripDetails;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by m.ramin on 7/5/15.
 */
public class TripDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    TripDetails data;

    PublishSubject<Note> observableNoteSelection = PublishSubject.create();

    public void setData(TripDetails data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public Observable<Note> onNoteClicked() {
        return observableNoteSelection;
    }

    private Note getItem(int position) {
        return data.notes.get(position - 1);
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
        return data.notes.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ITEM_TYPE itemType = ITEM_TYPE.values()[getItemViewType(position)];
        switch (itemType) {
            case ITEM_HEADER:
                ((HeaderViewHolder) holder).description.setText(data.content);
                break;
            case ITEM_NOTE:
                Note note = getItem(position);

                ((NoteViewHolder) holder).title.setText(note.title);
                ((NoteViewHolder) holder).content.setText(note.content);
                ((NoteViewHolder) holder).created.setText(note.note_date.toString());
                break;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
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

        public NoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

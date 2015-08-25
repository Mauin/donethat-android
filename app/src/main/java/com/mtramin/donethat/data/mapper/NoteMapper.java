package com.mtramin.donethat.data.mapper;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.mtramin.donethat.data.model.Note;
import com.mtramin.donethat.data.persist.realm.NoteDto;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by m.ramin on 8/8/15.
 */
public final class NoteMapper {

    private NoteMapper() {
        // Empty
    }

    public static RealmList<NoteDto> createNoteDtoList(List<Note> notes) {
        RealmList<NoteDto> dtoList = new RealmList<>();
        for (Note note : notes) {
            dtoList.add(createNoteDto(note));
        }
        return dtoList;
    }

    public static NoteDto createNoteDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.id.toString());
        dto.setTitle(note.title);
        dto.setContent(note.content);
        dto.setDate(note.date.getMillis());
        dto.setUpdated(note.updated.getMillis());

        if (note.location != null) {
            dto.setLatitude(note.location.latitude);
            dto.setLongitude(note.location.longitude);
        }

        if (note.image != null) {
            dto.setImage(note.image.toString());
        }

        return dto;
    }

    public static NoteDto createNoteDto(UUID tripId, Note note) {
        NoteDto dto = new NoteDto();
        dto.setTripId(tripId.toString());
        dto.setId(note.id.toString());
        dto.setTitle(note.title);
        dto.setContent(note.content);
        dto.setDate(note.date.getMillis());
        dto.setUpdated(note.updated.getMillis());

        if (note.location != null) {
            dto.setLatitude(note.location.latitude);
            dto.setLongitude(note.location.longitude);
        }

        if (note.image != null) {
            dto.setImage(note.image.toString());
        }

        return dto;
    }

    public static Note toNote(NoteDto dto) {
        Note.Builder builder = new Note.Builder()
                .id(UUID.fromString(dto.getId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .date(new DateTime(dto.getDate()))
                .updated(new DateTime(dto.getUpdated()));

        if (!TextUtils.isEmpty(dto.getImage())) {
            builder.image(Uri.parse(dto.getImage()));
        }

        if (dto.getLatitude() != 0.0f && dto.getLongitude() != 0.0f) {
            builder.location(new LatLng(dto.getLatitude(), dto.getLongitude()));
        }

        return builder.build();
    }

    public static List<Note> toNoteList(RealmList<NoteDto> notes) {
        List<Note> result = new ArrayList<>();
        for (NoteDto dto : notes) {
            result.add(toNote(dto));
        }
        return result;
    }
}

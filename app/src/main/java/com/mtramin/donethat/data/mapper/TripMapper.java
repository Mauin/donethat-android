package com.mtramin.donethat.data.mapper;

import com.mtramin.donethat.data.model.Trip;
import com.mtramin.donethat.data.persist.realm.TripDto;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmResults;

/**
 * Created by m.ramin on 8/8/15.
 */
public final class TripMapper {
    private TripMapper() {
        // EMPTY
    }

    public static TripDto toTripDto(Trip trip) {
        TripDto dto = new TripDto();
        dto.setTitle(trip.title);
        dto.setContent(trip.content);
        dto.setDate(trip.date.getMillis());
        dto.setUpdated(trip.updated.getMillis());
        dto.setId(trip.id.toString());
        return dto;
    }

    public static Trip toTrip(TripDto result) {
        return new Trip.Builder()
                .id(UUID.fromString(result.getId()))
                .title(result.getTitle())
                .content(result.getContent())
                .date(new DateTime(result.getDate()))
                .updated(new DateTime(result.getDate()))
                .build();
    }

    public static List<TripDto> createTripDtoList(List<Trip> trips) {
        List<TripDto> dtoList = new ArrayList<>();
        for (Trip trip : trips) {
            dtoList.add(toTripDto(trip));
        }
        return dtoList;
    }

    public static List<Trip> toTripList(RealmResults<TripDto> results) {
        List<Trip> trips = new ArrayList<>(results.size());

        for (TripDto result : results) {
            trips.add(TripMapper.toTrip(result));
        }
        return trips;
    }
}

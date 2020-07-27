package com.pacific.volcano.campsitereservations.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ReservationSpotGenerator {
    public static Set<ReservationSpot> spotsFrom(LocalDate arrivalDate, LocalDate departureDate,
                                                 Reservation reservation) {
        Set<ReservationSpot> spots = new HashSet<>();
        while (!arrivalDate.isAfter(departureDate)) {
            spots.add(ReservationSpot.builder().reservedDate(arrivalDate).reservation(reservation).build());
            arrivalDate = arrivalDate.plusDays(1);
        }
        return spots;
    }

    public static Set<LocalDate> dateListBetween(LocalDate from, LocalDate to) {
        return from.datesUntil(to).collect(Collectors.toSet());
    }
}

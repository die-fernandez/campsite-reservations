package com.pacific.volcano.campsitereservations.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class ReservationResponse {

    private String id;
    private String email;
    private String fullName;
    private List<LocalDate> reservedDays;

    @JsonIgnore
    public static ReservationResponse createFrom(Reservation reservation) {
        return ReservationResponse.builder()
                .email(reservation.getEmail())
                .fullName(reservation.getFullName())
                .reservedDays(reservation.getReservationSpots().stream()
                        .map(ReservationSpot::getReservedDate)
                        .sorted()
                        .collect(Collectors.toList()))
                .id(reservation.getId()).build();
    }
}

package com.pacific.volcano.campsitereservations.controller;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class AvailabilityResponse {
    LocalDate from;
    LocalDate to;
    List<LocalDate> availableDates;

    public static AvailabilityResponse createFrom(Set<LocalDate> availabilityBetween, LocalDate from, LocalDate to) {
        return AvailabilityResponse.builder()
                .availableDates(availabilityBetween.stream().sorted().collect(Collectors.toList()))
                .from(from).to(to)
                .build();
    }
}

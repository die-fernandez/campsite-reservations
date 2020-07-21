package com.pacific.volcano.campsitereservations.api;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateReservationRequest {

    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String fullname;

}

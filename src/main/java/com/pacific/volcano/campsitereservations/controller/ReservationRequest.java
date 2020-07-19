package com.pacific.volcano.campsitereservations.controller;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String email;
    private String fullname;
}

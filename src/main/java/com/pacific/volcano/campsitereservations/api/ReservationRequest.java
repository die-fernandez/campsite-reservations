package com.pacific.volcano.campsitereservations.api;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservationRequest {
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String email;
    private String fullname;
}

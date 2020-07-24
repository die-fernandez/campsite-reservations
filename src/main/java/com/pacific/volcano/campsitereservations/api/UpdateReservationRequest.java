package com.pacific.volcano.campsitereservations.api;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class UpdateReservationRequest implements DateRangeValidatable {

    private LocalDate arrivalDate;
    private LocalDate departureDate;
    @NotBlank(message = "Fullname name is mandatory")
    private String fullname;

}

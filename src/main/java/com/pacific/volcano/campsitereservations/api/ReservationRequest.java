package com.pacific.volcano.campsitereservations.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pacific.volcano.campsitereservations.validation.ConsistentDateParameters;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
@ConsistentDateParameters
public class ReservationRequest implements DateRangeValidatable {
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    @NotBlank(message = "Email name is mandatory")
    private String email;
    @NotBlank(message = "Full name is mandatory")
    private String fullname;

    @Override
    @JsonIgnore
    public LocalDate getFrom() {
        return getArrivalDate();
    }

    @Override
    @JsonIgnore
    public LocalDate getTo() {
        return getDepartureDate();
    }
}

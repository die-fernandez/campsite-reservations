package com.pacific.volcano.campsitereservations.api;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.beans.Transient;
import java.time.LocalDate;

@Data
@Builder
public class UpdateReservationRequest implements DateRangeValidatable {

    private LocalDate arrivalDate;
    private LocalDate departureDate;
    @NotBlank(message = "Fullname name is mandatory")
    private String fullname;

    @Override
    @Transient
    public LocalDate getFrom() {
        return getArrivalDate();
    }

    @Override
    @Transient
    public LocalDate getTo() {
        return getDepartureDate();
    }

}

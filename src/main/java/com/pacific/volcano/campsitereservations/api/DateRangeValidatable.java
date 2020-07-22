package com.pacific.volcano.campsitereservations.api;

import java.time.LocalDate;

public interface DateRangeValidatable {
    LocalDate getArrivalDate();
    LocalDate getDepartureDate();
}

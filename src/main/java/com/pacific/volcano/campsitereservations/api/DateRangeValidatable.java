package com.pacific.volcano.campsitereservations.api;

import java.time.LocalDate;

public interface DateRangeValidatable {
    LocalDate getFrom();
    LocalDate getTo();
}

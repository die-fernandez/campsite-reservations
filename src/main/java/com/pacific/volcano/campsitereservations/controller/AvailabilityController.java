package com.pacific.volcano.campsitereservations.controller;

import com.pacific.volcano.campsitereservations.api.AvailabilityResponse;
import com.pacific.volcano.campsitereservations.domain.DateRange;
import com.pacific.volcano.campsitereservations.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Future;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/availability", produces = MediaType.APPLICATION_JSON_VALUE)
public class AvailabilityController {

    private final ReservationService reservationService;

    /**
     * Queries for campsite availability on a given range
     *
     * @param from
     * @param to
     * @return AvailabilityResponse
     */
    @GetMapping
    public ResponseEntity<AvailabilityResponse> findAvailability(
            @RequestParam(required = false) @Future @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Future @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        DateRange dateRange = completeDateRange(DateRange.builder().from(from).to(to).build());
        return ResponseEntity.ok(AvailabilityResponse
                .createFrom(this.reservationService.findAvailabilityBetween(dateRange), from, to));
    }

    private DateRange completeDateRange(DateRange dateRange) {
        if (dateRange.getFrom() == null && dateRange.getTo() == null) {
            return DateRange.builder().from(LocalDate.now()).to(LocalDate.now().plusMonths(1)).build();
        }
        return dateRange;
    }

}

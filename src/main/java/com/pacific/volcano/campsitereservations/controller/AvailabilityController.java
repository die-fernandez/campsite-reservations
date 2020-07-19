package com.pacific.volcano.campsitereservations.controller;

import com.pacific.volcano.campsitereservations.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/availability", produces = MediaType.APPLICATION_JSON_VALUE)
public class AvailabilityController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<AvailabilityResponse> findAvailability(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to) {
        return ResponseEntity.ok(AvailabilityResponse.createFrom(this.reservationService.findAvailabilityBetween(from,to),from,to));
    }
}

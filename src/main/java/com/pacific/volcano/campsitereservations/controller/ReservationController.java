package com.pacific.volcano.campsitereservations.controller;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.ReservationResponse;
import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.exception.NoAvailabilityException;
import com.pacific.volcano.campsitereservations.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationRequest reservationRequest) {
        Reservation reservationResult = reservationService.create(reservationRequest);
        return ResponseEntity.ok(ReservationResponse.createFrom(reservationResult));
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus
    public ResponseEntity<ReservationResponse> update(@RequestBody UpdateReservationRequest updateReservationRequest, @PathVariable("id") Long id)
                    throws NoAvailabilityException {
        Reservation reservationResult = reservationService.update(updateReservationRequest,id);
        return ResponseEntity.ok(ReservationResponse.createFrom(reservationResult));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReservationResponse> find(@PathVariable("id") Long id) {
        Reservation reservation = reservationService.find(id);
        return ResponseEntity.ok(ReservationResponse.createFrom(reservation));
    }

}

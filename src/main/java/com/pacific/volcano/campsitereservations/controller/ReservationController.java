package com.pacific.volcano.campsitereservations.controller;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.ReservationResponse;
import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.exception.CampsiteUnavailableException;
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
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static com.pacific.volcano.campsitereservations.api.ReservationResponse.*;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReservationResponse> create(@RequestBody @Valid ReservationRequest reservationRequest) {
        try {
            Reservation reservationResult = reservationService.create(reservationRequest);
            return ResponseEntity.ok(createFrom(reservationResult));
        } catch (CampsiteUnavailableException exc) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, exc.getMessage(), exc);
            }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ReservationResponse> update(@RequestBody @Valid UpdateReservationRequest updateReservationRequest, @PathVariable("id") Long id) {
        try {
            Reservation reservationResult = reservationService.update(updateReservationRequest, id);
            return ResponseEntity.ok(createFrom(reservationResult));
        } catch (CampsiteUnavailableException exc) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exc.getMessage(), exc);
        }


    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReservationResponse> find(@PathVariable("id") Long id) {
        Reservation reservation = reservationService.find(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested reservation does not exist"));
        return ResponseEntity.ok(createFrom(reservation));
    }

}

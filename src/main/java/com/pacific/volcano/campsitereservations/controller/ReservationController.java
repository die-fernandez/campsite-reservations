package com.pacific.volcano.campsitereservations.controller;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.ReservationResponse;
import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.service.ReservationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.pacific.volcano.campsitereservations.api.ReservationResponse.createFrom;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * creates a reservation for the campsite on the given dates
     *
     * @param reservationRequest
     * @return the ReservationResponse with an id or an error if the reservation was unsuccessful
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReservationResponse> create(@RequestBody @Valid ReservationRequest reservationRequest) {
        Reservation reservationResult = reservationService.create(reservationRequest);
        log.info("Created new reservation with id {}", reservationResult.getId());
        return ResponseEntity.ok(createFrom(reservationResult));
    }


    /**
     * cancels the reservation with the given id. Note that the reservation will still exist in the store,
     * with no associated days.
     * @param id
     * @return the ReservationResponse in inactive state or an error if the cancel was unsuccessful.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable("id") Long id) {
        Reservation reservation = reservationService.cancel(id);
        log.info("Cancelling new reservation with id {}", reservation.getId());
        return ResponseEntity.ok(createFrom(reservation));
    }

    /**
     * updates a campsite reservation for a given id, can change name and days but not email.
     * @param updateReservationRequest
     * @param id
     * @return the updated ReservationResponse or an error if the reservation could not be updated.
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<ReservationResponse> update(
            @RequestBody @Valid UpdateReservationRequest updateReservationRequest, @PathVariable("id") Long id) {
        Reservation reservationResult = reservationService.update(updateReservationRequest, id);
        log.info("Updated new reservation with id {}", reservationResult.getId());
        return ResponseEntity.ok(createFrom(reservationResult));
    }

    /**
     * returns the reservation associated with id
     * @param id
     * @return ResponseEntity<ReservationResponse>
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ReservationResponse> find(@PathVariable("id") Long id) {
        return ResponseEntity.ok(createFrom(reservationService.find(id)));
    }

    /**
     * returns all the reservations. Includes cancelled reservations
     * @return
     */
    @GetMapping()
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> responseList = reservationService.findAll()
                .stream().map(ReservationResponse::createFrom)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }


}

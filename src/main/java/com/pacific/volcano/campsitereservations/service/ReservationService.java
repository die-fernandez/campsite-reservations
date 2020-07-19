package com.pacific.volcano.campsitereservations.service;

import com.pacific.volcano.campsitereservations.controller.ReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.domain.ReservationSpotGenerator;
import com.pacific.volcano.campsitereservations.repository.ReservationRepository;
import com.pacific.volcano.campsitereservations.repository.ReservationSpotRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pacific.volcano.campsitereservations.domain.ReservationSpotGenerator.*;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private ReservationSpotRepository reservationSpotRepository;


    public Reservation create(ReservationRequest reservationRequest) {
        Reservation newReservation = Reservation.builder()
                .email(reservationRequest.getEmail())
                .fullName(reservationRequest.getFullname())
                .build();
        newReservation.setReservationSpots(spotsFrom(reservationRequest.getArrivalDate(),reservationRequest.getDepartureDate(),newReservation));
        return reservationRepository.save(newReservation);
    }

    public Set<LocalDate> findAvailabilityBetween(LocalDate from, LocalDate to) {
        Set<ReservationSpot> occupancy = this.reservationSpotRepository.findByReservedDateIsBetween(from, to);
        Set<LocalDate> occupancyDates = occupancy.stream().map(ReservationSpot::getReservedDate).collect(Collectors.toSet());
        Set<LocalDate> requestedDates = from.datesUntil(to).collect(Collectors.toSet());
        return SetUtils.difference(requestedDates,occupancyDates);
    }
}

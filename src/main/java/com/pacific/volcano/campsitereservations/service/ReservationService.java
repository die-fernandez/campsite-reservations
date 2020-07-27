package com.pacific.volcano.campsitereservations.service;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.exception.CampsiteConcurrentReservation;
import com.pacific.volcano.campsitereservations.exception.CampsiteUnavailableException;
import com.pacific.volcano.campsitereservations.exception.ReservationNotFoundException;
import com.pacific.volcano.campsitereservations.repository.ReservationRepository;
import com.pacific.volcano.campsitereservations.repository.ReservationSpotRepository;
import dto.DateRangeDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pacific.volcano.campsitereservations.domain.ReservationSpotGenerator.spotsFrom;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class ReservationService {

    private ReservationRepository reservationRepository;
    private ReservationSpotRepository reservationSpotRepository;


    public Reservation create(ReservationRequest reservationRequest) {
        //check availability before submitting creation
        //we let the database handle the concurrency
        log.info("Creating new reservation for request {}", reservationRequest.toString());
        if (spotsAlreadyReserved(reservationRequest)) {
            throw new CampsiteUnavailableException("No availability for selected dates");
        }
        Reservation newReservation = generateReservation(reservationRequest);
        //at this point reservation could still fail if there's another attempt at the same time
        try {
            return reservationRepository.save(newReservation);
        } catch (DataIntegrityViolationException exc) {
            log.error("Concurrent reservation", exc);
            throw new CampsiteConcurrentReservation("There was a problem while trying to reserve your spots, please try again", exc);
        }
    }

    private Reservation generateReservation(ReservationRequest reservationRequest) {
        Reservation newReservation = Reservation.builder()
                .email(reservationRequest.getEmail())
                .fullName(reservationRequest.getFullname())
                .active(true)
                .build();
        newReservation.setReservationSpots(spotsFrom(reservationRequest.getArrivalDate(), reservationRequest.getDepartureDate(), newReservation));
        return newReservation;
    }

    public Set<LocalDate> findAvailabilityBetween(@Valid DateRangeDto dateRange) {
        LocalDate adjustedTo = dateRange.getTo().plusDays(1);
        Set<ReservationSpot> occupancy = this.reservationSpotRepository.findByReservedDateIsBetween(dateRange.getFrom(), adjustedTo);
        Set<LocalDate> occupancyDates = occupancy.stream().map(ReservationSpot::getReservedDate).collect(Collectors.toSet());
        Set<LocalDate> requestedDates = dateRange.getFrom().datesUntil(adjustedTo).collect(Collectors.toSet());
        return SetUtils.difference(requestedDates, occupancyDates);
    }

    public Reservation find(Long id) {
        return this.reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("No active reservation found for the given ID"));
    }

    public Reservation update(UpdateReservationRequest updateReservationRequest, Long id) {
        Optional<Reservation> reservationOptional = this.reservationRepository.findById(id);
        if (reservationOptional.isEmpty() || !reservationOptional.get().isActive()) {
            throw new ReservationNotFoundException("No active reservation found for the given ID");
        }
        Reservation reservation = reservationOptional.get();
        //check availability before submitting update to avoid losing old reserved spots in case of no avail
        if (spotsAlreadyReserved(updateReservationRequest, id)) {
            log.warn("No availability for selectedDates for reservation update attempt. Id: {}", reservation.getId());
            throw new CampsiteUnavailableException("No availability for selected dates");
        }
        this.updateReservationInfo(updateReservationRequest, reservation);
        //at this point reservations could still fail if there's another attempt at the same time
        //we let the database handle the concurrency
        try {
            return reservationRepository.save(reservation);
        } catch (DataIntegrityViolationException exc) {
            log.error("Concurrent reservation", exc);
            throw new CampsiteConcurrentReservation("There was a problem while trying to reserve your spots, please try again", exc);
        }
    }

    private boolean spotsAlreadyReserved(UpdateReservationRequest updateReservationRequest, Long id) {
        Set<ReservationSpot> alreadyReservedSpots = reservationSpotRepository
                .findByReservedDateIsBetween(updateReservationRequest.getArrivalDate(),
                        updateReservationRequest.getDepartureDate()).stream()
                .filter(i -> !i.getId().equals(id)).collect(Collectors.toSet());
        return CollectionUtils.isNotEmpty(alreadyReservedSpots);
    }

    private boolean spotsAlreadyReserved(ReservationRequest reservationRequest) {
        Set<ReservationSpot> alreadyReservedSpots = reservationSpotRepository
                .findByReservedDateIsBetween(reservationRequest.getArrivalDate(),
                        reservationRequest.getDepartureDate());
        return CollectionUtils.isNotEmpty(alreadyReservedSpots);
    }

    private void updateReservationInfo(UpdateReservationRequest updateReservationRequest, Reservation reservation) {
        Set<ReservationSpot> currentReservedSpots = reservation.getReservationSpots();
        //merging spots
        Set<ReservationSpot> updatedSpots = spotsFrom(updateReservationRequest.getArrivalDate(), updateReservationRequest.getDepartureDate(), reservation);
        currentReservedSpots.removeIf(i -> !updatedSpots.contains(i));
        currentReservedSpots.addAll(updatedSpots);
        if (StringUtils.isNotBlank(updateReservationRequest.getFullname())) {
            reservation.setFullName(updateReservationRequest.getFullname());
        }
    }

    public List<Reservation> findAll() {
        return this.reservationRepository.findAll();
    }

    public Reservation cancel(Long id) {
        Optional<Reservation> reservationOptional = this.reservationRepository.findById(id);
        if (reservationOptional.isEmpty() || !reservationOptional.get().isActive()) {
            log.warn("No active reservation found for cancel attempt. Id: {}", id);
            throw new ReservationNotFoundException("No active reservation found for the given ID");
        }
        Reservation reservationToCancel = reservationOptional.get();
        reservationToCancel.getReservationSpots().clear();
        reservationToCancel.setActive(Boolean.FALSE);
        return reservationRepository.save(reservationToCancel);
    }
}

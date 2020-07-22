package com.pacific.volcano.campsitereservations.service;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.exception.CampsiteUnavailableException;
import com.pacific.volcano.campsitereservations.repository.ReservationRepository;
import com.pacific.volcano.campsitereservations.repository.ReservationSpotRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pacific.volcano.campsitereservations.domain.ReservationSpotGenerator.spotsFrom;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private ReservationSpotRepository reservationSpotRepository;


    public Reservation create(ReservationRequest reservationRequest) {
        Set<ReservationSpot> alreadyReservedSpots = reservationSpotRepository
                        .findByReservedDateIsBetween(reservationRequest.getArrivalDate(),
                                        reservationRequest.getDepartureDate());
        if(CollectionUtils.isNotEmpty(alreadyReservedSpots)) {
            throw new CampsiteUnavailableException("No availability for selected dates");
        }
        Reservation newReservation = Reservation.builder()
                .email(reservationRequest.getEmail())
                .fullName(reservationRequest.getFullname())
                .build();
        newReservation.setReservationSpots(spotsFrom(reservationRequest.getArrivalDate(),reservationRequest.getDepartureDate(),newReservation));
        try {
            return reservationRepository.save(newReservation);
        } catch(DataIntegrityViolationException exc) {
            throw new CampsiteUnavailableException("No availability for selected dates",exc);
        }
    }

    public Set<LocalDate> findAvailabilityBetween(LocalDate from, LocalDate to) {
        Set<ReservationSpot> occupancy = this.reservationSpotRepository.findByReservedDateIsBetween(from, to);
        Set<LocalDate> occupancyDates = occupancy.stream().map(ReservationSpot::getReservedDate).collect(Collectors.toSet());
        Set<LocalDate> requestedDates = from.datesUntil(to).collect(Collectors.toSet());
        return SetUtils.difference(requestedDates,occupancyDates);
    }

    public Optional<Reservation> find(Long id) {
        return this.reservationRepository.findById(id);
    }

    public Reservation update(UpdateReservationRequest updateReservationRequest, Long id) {
        //check availability before submitting update to avoid losing old reserved spots in case of no avail
        Set<ReservationSpot> alreadyReservedSpots = reservationSpotRepository
                        .findByReservedDateIsBetween(updateReservationRequest.getArrivalDate(),
                                        updateReservationRequest.getDepartureDate()).stream()
                        .filter(i -> !i.getId().equals(id)).collect(Collectors.toSet());
        if(CollectionUtils.isNotEmpty(alreadyReservedSpots)) {
            throw new CampsiteUnavailableException("No availability for selected dates");
        }
        Reservation reservation = this.reservationRepository.getOne(id);
        this.updateReservationInfo(updateReservationRequest, reservation);
        try {
            return reservationRepository.save(reservation);
        } catch(DataIntegrityViolationException exc) {
            throw new CampsiteUnavailableException("No availability for selected dates",exc);
        }
    }

    private void updateReservationInfo(UpdateReservationRequest updateReservationRequest, Reservation reservation) {
        Set<ReservationSpot> currentReservedSpots = reservation.getReservationSpots();
        //merging spots
        Set<ReservationSpot> updatedSpots = spotsFrom(updateReservationRequest.getArrivalDate(),updateReservationRequest.getDepartureDate(),reservation);
        currentReservedSpots.removeIf(i-> !updatedSpots.contains(i));
        currentReservedSpots.addAll(updatedSpots);
        if (StringUtils.isNotBlank(updateReservationRequest.getFullname())) {
            reservation.setFullName(updateReservationRequest.getFullname());
        }
    }

}

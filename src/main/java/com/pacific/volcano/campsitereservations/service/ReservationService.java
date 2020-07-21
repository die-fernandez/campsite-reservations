package com.pacific.volcano.campsitereservations.service;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.domain.ReservationSpotGenerator;
import com.pacific.volcano.campsitereservations.exception.NoAvailabilityException;
import com.pacific.volcano.campsitereservations.repository.ReservationRepository;
import com.pacific.volcano.campsitereservations.repository.ReservationSpotRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pacific.volcano.campsitereservations.domain.ReservationSpotGenerator.spotsFrom;

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

    //TODO handle not found
    public Reservation find(Long id) {
        return this.reservationRepository.findById(id).orElse(null);
    }

    public Reservation update(UpdateReservationRequest updateReservationRequest, Long id) throws NoAvailabilityException {
        //check availability before submitting update to avoid losing old reserved spots in case of no avail
        Set<ReservationSpot> alreadyReservedSpots = reservationSpotRepository
                        .findByReservedDateIsBetween(updateReservationRequest.getArrivalDate(),
                                        updateReservationRequest.getDepartureDate()).stream()
                        .filter(i -> !i.getId().equals(id)).collect(Collectors.toSet());
        if(CollectionUtils.isNotEmpty(alreadyReservedSpots)) {
            throw new NoAvailabilityException("No availability for selected dates");
        }
        Reservation reservation = this.reservationRepository.getOne(id);
        Set<ReservationSpot> currentReservedSpots = reservation.getReservationSpots();
        //merging spots
        Set<ReservationSpot> updatedSpots = ReservationSpotGenerator.spotsFrom(updateReservationRequest.getArrivalDate(),updateReservationRequest.getDepartureDate(),reservation);
        currentReservedSpots.removeIf(i-> !updatedSpots.contains(i));
        currentReservedSpots.addAll(updatedSpots);
        if (StringUtils.isNotBlank(updateReservationRequest.getFullname())) {
            reservation.setFullName(updateReservationRequest.getFullname());
        }

        this.reservationRepository.save(reservation);
        return reservation;
    }
}

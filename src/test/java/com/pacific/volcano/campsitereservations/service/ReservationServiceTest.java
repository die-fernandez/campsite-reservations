package com.pacific.volcano.campsitereservations.service;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.DateRange;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.exception.CampsiteUnavailableException;
import com.pacific.volcano.campsitereservations.exception.ReservationNotFoundException;
import com.pacific.volcano.campsitereservations.repository.ReservationRepository;
import com.pacific.volcano.campsitereservations.repository.ReservationSpotRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pacific.volcano.campsitereservations.domain.ReservationSpot.builder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private final LocalDate today = LocalDate.now();
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReservationSpotRepository reservationSpotRepository;
    Set<ReservationSpot> reservationSpots;
    private ReservationService reservationService;

    @BeforeEach
    void setup() {

        List<LocalDate> validDatesList = today.plusDays(3).datesUntil(today.plusDays(6)).collect(Collectors.toList());
        List<ReservationSpot> reservedSpots = validDatesList.stream().map(i -> builder().reservedDate(i).build())
                .collect(Collectors.toList());

        reservationService = new ReservationService(reservationRepository, reservationSpotRepository);

        Reservation reservation = Reservation.builder().fullName("name1").email("email").active(true).build();
        reservationSpots =
                new HashSet<>(reservedSpots);
        reservation.setReservationSpots(reservationSpots);


    }

    @Test
    void testFindAvailabilityBetween_noAvail() {
        when(reservationSpotRepository.findByReservedDateIsBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reservationSpots);

        DateRange dateRange = DateRange.builder().from(today.plusDays(3)).to(today.plusDays(5)).build();
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(dateRange);
        Assertions.assertTrue(availabilityBetween.isEmpty());

    }

    @Test
    void testFindAvailabilityBetween() {
        when(reservationSpotRepository.findByReservedDateIsBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reservationSpots);

        DateRange dateRange = DateRange.builder().from(today.plusDays(10)).to(today.plusDays(12)).build();
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(dateRange);
        Assertions.assertEquals(3, availabilityBetween.size());
    }

    @Test
    void testFindAvailabilityBetween_partialAvail() {
        when(reservationSpotRepository.findByReservedDateIsBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reservationSpots);

        DateRange dateRange = DateRange.builder().from(today.plusDays(4)).to(today.plusDays(6)).build();
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(dateRange);
        Assertions.assertEquals(1, availabilityBetween.size());
    }

    @Test
    void testUpdateReservation_notFound() {
        Long nonexistentId = 1L;
        Mockito.when(reservationRepository.findById(nonexistentId)).thenReturn(Optional.empty());
        UpdateReservationRequest updateReservationRequest = UpdateReservationRequest.builder().build();
        assertThrows(ReservationNotFoundException.class, () -> reservationService
                .update(updateReservationRequest, nonexistentId));
    }

    @Test
    void testUpdateReservation_inactive() {
        Long inactiveId = 1L;
        Mockito.when(reservationRepository.findById(inactiveId))
                .thenReturn(Optional.of(Reservation.builder().active(false).build()));
        UpdateReservationRequest updateReservationRequest = UpdateReservationRequest.builder().build();
        assertThrows(ReservationNotFoundException.class, () -> reservationService
                .update(updateReservationRequest, inactiveId));
    }

    @Test
    void testCreateReservation_unavailableDates() {
        LocalDate from = LocalDate.now().plusDays(2);
        LocalDate to = LocalDate.now().plusDays(5);
        ReservationSpot reservedSpot = builder().reservedDate(from).build();
        Mockito.when(reservationSpotRepository.findByReservedDateIsBetween(from, to))
                .thenReturn(Sets.newSet(reservedSpot));
        ReservationRequest reservationRequest = ReservationRequest.builder().arrivalDate(from).departureDate(to)
                .build();
        assertThrows(CampsiteUnavailableException.class, () -> reservationService.create(reservationRequest));
    }

    @Test
    void testCreateReservation_ok() {
        LocalDate from = LocalDate.now().plusDays(2);
        LocalDate to = LocalDate.now().plusDays(5);
        ReservationSpot reservedSpot = builder().reservedDate(from).build();
        Mockito.when(reservationSpotRepository.findByReservedDateIsBetween(from, to))
                .thenReturn(Sets.newSet(reservedSpot));
        ReservationRequest reservationRequest = ReservationRequest.builder().arrivalDate(from).departureDate(to)
                .build();
        assertThrows(CampsiteUnavailableException.class, () -> reservationService.create(reservationRequest));
    }

    @Test
    void testCancelReservation_notFound() {
        Long nonexistent = 33L;
        Mockito.when(reservationRepository.findById(nonexistent)).thenReturn(Optional.empty());
        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancel(nonexistent));
    }

    @Test
    void testCancelReservation_inactive() {
        Long inactiveId = 1L;
        Mockito.when(reservationRepository.findById(inactiveId))
                .thenReturn(Optional.of(Reservation.builder().active(false).build()));
        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancel(inactiveId));
    }

    @Test
    void testCancelReservation_ok() {
        Long reservationId = 1L;
        ReservationSpot reservedSpot = builder().reservedDate(LocalDate.now().plusDays(1)).build();
        Reservation reservationToSave = Reservation.builder()
                .reservationSpots(Sets.newSet(reservedSpot))
                .active(true)
                .build();
        Mockito.when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservationToSave));
        Mockito.when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);
        Reservation canceled = reservationService.cancel(reservationId);
        assertFalse(canceled.isActive());
        assertTrue(canceled.getReservationSpots().isEmpty());
    }

}

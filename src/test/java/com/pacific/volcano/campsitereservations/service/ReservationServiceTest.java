package com.pacific.volcano.campsitereservations.service;

import com.pacific.volcano.campsitereservations.api.UpdateReservationRequest;
import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.exception.ReservationNotFoundException;
import com.pacific.volcano.campsitereservations.repository.ReservationRepository;
import com.pacific.volcano.campsitereservations.repository.ReservationSpotRepository;
import dto.DateRangeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pacific.volcano.campsitereservations.domain.ReservationSpot.builder;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReservationSpotRepository reservationSpotRepository;

    private ReservationService reservationService;

    private final LocalDate today = LocalDate.now();

    Set<ReservationSpot> reservationSpots;

    @BeforeEach
    void setup() {

        List<LocalDate> validDatesList = today.plusDays(3).datesUntil(today.plusDays(6)).collect(Collectors.toList());
        List<ReservationSpot> reservedSpots = validDatesList.stream().map(i -> builder().reservedDate(i).build())
                        .collect(Collectors.toList());

        reservationService = new ReservationService(reservationRepository,reservationSpotRepository);

        Reservation reservation = Reservation.builder().fullName("name1").email("email").active(true).build();
         reservationSpots =
                        new HashSet<>(reservedSpots);
        reservation.setReservationSpots(reservationSpots);


    }

    @Test void testFindAvailabilityBetween_noAvail() {
        when(reservationSpotRepository.findByReservedDateIsBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(reservationSpots);

        DateRangeDto dateRangeDto = DateRangeDto.builder().from(today.plusDays(3)).to(today.plusDays(5)).build();
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(dateRangeDto);
        Assertions.assertTrue(availabilityBetween.isEmpty());

    }

    @Test void testFindAvailabilityBetween_Avail() {
        when(reservationSpotRepository.findByReservedDateIsBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(reservationSpots);

        DateRangeDto dateRangeDto = DateRangeDto.builder().from(today.plusDays(10)).to(today.plusDays(12)).build();
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(dateRangeDto);
        Assertions.assertEquals(3,availabilityBetween.size());
    }

    @Test void testFindAvailabilityBetween_partialAvail() {
        when(reservationSpotRepository.findByReservedDateIsBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(reservationSpots);

        DateRangeDto dateRangeDto = DateRangeDto.builder().from(today.plusDays(4)).to(today.plusDays(6)).build();
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(dateRangeDto);
        Assertions.assertEquals(1,availabilityBetween.size());
    }

    @Test
    void testUpdateReservation_notFound() {
        Long nonexistentId = 1l;
        Mockito.when(reservationRepository.findById(nonexistentId)).thenReturn(Optional.empty());
        UpdateReservationRequest updateReservationRequest = UpdateReservationRequest.builder().build();
        Assertions.assertThrows(ReservationNotFoundException.class,()->reservationService.update(updateReservationRequest,nonexistentId));
    }

    @Test
    void testUpdateReservation_inactive() {
        Long inactiveId = 1l;
        Mockito.when(reservationRepository.findById(inactiveId)).thenReturn(Optional.of(Reservation.builder().active(false).build()));
        UpdateReservationRequest updateReservationRequest = UpdateReservationRequest.builder().build();
        Assertions.assertThrows(ReservationNotFoundException.class,()->reservationService.update(updateReservationRequest,inactiveId));
    }


}

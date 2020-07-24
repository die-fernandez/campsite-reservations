package com.pacific.volcano.campsitereservations.service;

import com.pacific.volcano.campsitereservations.domain.Reservation;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.repository.ReservationRepository;
import com.pacific.volcano.campsitereservations.repository.ReservationSpotRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private LocalDate today = LocalDate.now();

    @BeforeEach
    void setup() {

        List<LocalDate> validDatesList = today.plusDays(3).datesUntil(today.plusDays(6)).collect(Collectors.toList());
        List<ReservationSpot> reservedSpots = validDatesList.stream().map(i -> builder().reservedDate(i).build())
                        .collect(Collectors.toList());

        reservationService = new ReservationService(reservationRepository,reservationSpotRepository);

        Reservation reservation = new Reservation();
        reservation.setFullName("name1");
        reservation.setEmail("email");
        Set<ReservationSpot> reservationSpots =
                        new HashSet<>(reservedSpots);
        reservation.setReservationSpots(reservationSpots);

        when(reservationSpotRepository.findByReservedDateIsBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(reservationSpots);

    }

    @Test void testFindAvailabilityBetween_noAvail() {
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(today.plusDays(3), today.plusDays(5));
        Assertions.assertTrue(availabilityBetween.isEmpty());

    }

    @Test void testFindAvailabilityBetween_Avail() {
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(today.plusDays(10), today.plusDays(12));
        Assertions.assertEquals(3,availabilityBetween.size());
    }

    @Test void testFindAvailabilityBetween_partialAvail() {
        Set<LocalDate> availabilityBetween = reservationService.findAvailabilityBetween(today.plusDays(4), today.plusDays(6));
        Assertions.assertEquals(1,availabilityBetween.size());
    }


    //TODO add tests validating update when CampsiteUnavailableException, DataIntegrityViolationException
    //@Test void update() {
        //fill me


}

package com.pacific.volcano.campsitereservations;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.exception.CampsiteConcurrentReservation;
import com.pacific.volcano.campsitereservations.exception.CampsiteUnavailableException;
import com.pacific.volcano.campsitereservations.service.ReservationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CampsiteReservationsApplicationTests {

	/*
	This test class attempts to force the occurrence of a constraint violation exception when several threads try to make a reservation at the same time
	Since we are letting the ddbb handle the lock on the resource, this should be enough proof that we are not generating reservations with overlapping days.

	The test could eventually fail if the exceptions are not triggered for any reason.
	 */

	@Autowired
	ReservationService reservationService;

	private AtomicBoolean gotConcurrentException;
	private AtomicBoolean gotCampsiteUnavailableException;

	@BeforeAll
	public void setup() {
		gotConcurrentException = new AtomicBoolean(Boolean.FALSE);
		gotCampsiteUnavailableException = new AtomicBoolean(Boolean.FALSE);
	}

	@Execution(CONCURRENT)
	@RepeatedTest(5)
	public void mainThread() {

		try {
			ReservationRequest reservation = ReservationRequest.builder().email("mail").fullname("name").arrivalDate(LocalDate.now().plusDays(2))
					.departureDate(LocalDate.now().plusDays(5)).build();
			reservationService.create(reservation);
		} catch (CampsiteConcurrentReservation e) {
			gotConcurrentException.set(Boolean.TRUE);
		} catch (CampsiteUnavailableException ex) {
			gotCampsiteUnavailableException.set(Boolean.TRUE);
		}
	}

	@Execution(CONCURRENT)
	@RepeatedTest(5)
	public void secondThread() {
		try {
			ReservationRequest reservation = ReservationRequest.builder().email("mail2").fullname("name2").arrivalDate(LocalDate.now().plusDays(2))
					.departureDate(LocalDate.now().plusDays(5)).build();
			reservationService.create(reservation);
		} catch (CampsiteConcurrentReservation e) {
			gotConcurrentException.set(Boolean.TRUE);
		} catch (CampsiteUnavailableException ex) {
			gotCampsiteUnavailableException.set(Boolean.TRUE);
		}
	}

	@Execution(CONCURRENT)
	@RepeatedTest(5)
	public void thirdThread() {
		try {
			ReservationRequest reservation = ReservationRequest.builder().email("mail3").fullname("name3").arrivalDate(LocalDate.now().plusDays(2))
					.departureDate(LocalDate.now().plusDays(5)).build();
			reservationService.create(reservation);
		} catch (CampsiteConcurrentReservation e) {
			gotConcurrentException.set(Boolean.TRUE);
		} catch (CampsiteUnavailableException ex) {
			gotCampsiteUnavailableException.set(Boolean.TRUE);
		}
	}

	@AfterAll
	public void after() {
		// checking no more than one reservation is created after all the test runs.
		Assertions.assertEquals(1,reservationService.findAll().size());
		// checking we got at least one occurrence of each expected exception.
		Assertions.assertTrue(gotCampsiteUnavailableException.get());
		Assertions.assertTrue(gotConcurrentException.get());
	}


}

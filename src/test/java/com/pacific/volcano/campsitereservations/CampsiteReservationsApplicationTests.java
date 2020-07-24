package com.pacific.volcano.campsitereservations;

import com.google.testing.threadtester.AnnotatedTestRunner;
import com.google.testing.threadtester.ThreadedBefore;
import com.google.testing.threadtester.ThreadedMain;
import com.google.testing.threadtester.ThreadedSecondary;
import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import com.pacific.volcano.campsitereservations.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class CampsiteReservationsApplicationTests {

	@Autowired
	ReservationService reservationService;
	ReservationRequest reservation;
	ReservationRequest reservation2;
	ReservationRequest reservation3;

	@ThreadedBefore
	public void before() {
		reservation = ReservationRequest.builder().email("mail").fullname("name").arrivalDate(LocalDate.now().plusDays(2))
						.departureDate(LocalDate.now().plusDays(5)).build();
		reservation2 = ReservationRequest.builder().email("mail2").fullname("name2").arrivalDate(LocalDate.now().plusDays(2))
						.departureDate(LocalDate.now().plusDays(5)).build();
	}

	@ThreadedMain
	public void mainThread() {
		reservationService.create(reservation);
	}

	@ThreadedSecondary
	public void secondThread() {
		reservationService.create(reservation2);
	}

	@Test
	void contextLoads() {

	}

	@Test
	void testConcurrentReservationCreation() {
		new AnnotatedTestRunner().runTests(this.getClass(), ReservationService.class);
	}

}

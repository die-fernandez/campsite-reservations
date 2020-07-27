package com.pacific.volcano.campsitereservations.config;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@Slf4j
public class DataInitConfig {
    //@Bean
    public CommandLineRunner loadData(ReservationService reservationService) {
        return (args) -> {
            log.info("initializing sample data");
            // save a couple of reservations
            reservationService.create(ReservationRequest.builder()
                    .email("email1")
                    .fullname("name1")
                    .arrivalDate(LocalDate.now().plusDays(3))
                    .departureDate(LocalDate.now().plusDays(5))
                    .build());

            reservationService.create(ReservationRequest.builder()
                    .email("email2")
                    .fullname("name2")
                    .arrivalDate(LocalDate.now().plusDays(10))
                    .departureDate(LocalDate.now().plusDays(12))
                    .build());

            reservationService.create(ReservationRequest.builder()
                    .email("email3")
                    .fullname("name3")
                    .arrivalDate(LocalDate.now().plusDays(21))
                    .departureDate(LocalDate.now().plusDays(23))
                    .build());

        };


    }
}

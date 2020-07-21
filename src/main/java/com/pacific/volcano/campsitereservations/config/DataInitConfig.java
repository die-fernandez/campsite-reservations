package com.pacific.volcano.campsitereservations.config;

import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@Slf4j
public class DataInitConfig {

    @Bean
    public CommandLineRunner loadData(ReservationService reservationService) {
        return (args) -> {
            log.info("initializing sample data");
            // save a couple of reservations
            reservationService.create(ReservationRequest.builder()
                            .email("email1")
                            .fullname("name1")
                            .arrivalDate(LocalDate.of(2020,8,2))
                            .departureDate(LocalDate.of(2020,8,4))
                            .build());

            reservationService.create(ReservationRequest.builder()
                            .email("email2")
                            .fullname("name2")
                            .arrivalDate(LocalDate.of(2020,8,12))
                            .departureDate(LocalDate.of(2020,8,14))
                            .build());

            reservationService.create(ReservationRequest.builder()
                            .email("email3")
                            .fullname("name3")
                            .arrivalDate(LocalDate.of(2020,8,22))
                            .departureDate(LocalDate.of(2020,8,24))
                            .build());
        };

    }
}

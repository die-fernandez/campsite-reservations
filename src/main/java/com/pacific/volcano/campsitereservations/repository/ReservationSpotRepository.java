package com.pacific.volcano.campsitereservations.repository;

import com.pacific.volcano.campsitereservations.domain.ReservationSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface ReservationSpotRepository  extends JpaRepository<ReservationSpot,Long> {

    Set<ReservationSpot> findByReservedDateIsBetween(LocalDate from, LocalDate to);
}

package com.pacific.volcano.campsitereservations.repository;

import com.pacific.volcano.campsitereservations.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
}

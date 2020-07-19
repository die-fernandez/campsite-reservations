package com.pacific.volcano.campsitereservations.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ReservationSpot {
    @Id
    private String id;
    @Column(unique = true)
    private LocalDate reservedDate;
    @ManyToOne
    private Reservation reservation;

}

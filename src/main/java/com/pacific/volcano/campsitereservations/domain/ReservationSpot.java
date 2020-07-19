package com.pacific.volcano.campsitereservations.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(unique = true)
    private LocalDate reservedDate;
    @ManyToOne
    private Reservation reservation;

}

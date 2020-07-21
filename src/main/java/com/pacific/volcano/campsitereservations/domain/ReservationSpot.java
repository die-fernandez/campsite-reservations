package com.pacific.volcano.campsitereservations.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class ReservationSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private LocalDate reservedDate;
    @ManyToOne
    private Reservation reservation;

}

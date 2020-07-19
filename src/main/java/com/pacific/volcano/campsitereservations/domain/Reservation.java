package com.pacific.volcano.campsitereservations.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    private String id;
    private String email;
    private String fullName;
    //audit
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @OneToMany(mappedBy="reservation")
    private Set<ReservationSpot> reservationSpots;
}

package com.pacific.volcano.campsitereservations.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class CampsiteConcurrentReservation extends RuntimeException {
    public CampsiteConcurrentReservation(String s, DataIntegrityViolationException exc) {
        super(s,exc);
    }
}

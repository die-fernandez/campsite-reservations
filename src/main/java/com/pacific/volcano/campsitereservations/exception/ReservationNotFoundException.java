package com.pacific.volcano.campsitereservations.exception;

public class ReservationNotFoundException
        extends RuntimeException {
    public ReservationNotFoundException(String description, Exception exc) {
        super(description, exc);
    }

    public ReservationNotFoundException(String description) {
        super(description);
    }
}

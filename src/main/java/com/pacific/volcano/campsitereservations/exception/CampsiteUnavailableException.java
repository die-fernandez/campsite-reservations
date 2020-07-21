package com.pacific.volcano.campsitereservations.exception;

public class NoAvailabilityException
                extends Exception {
    private static final long serialVersionUID = 2100275753007504979L;
    public NoAvailabilityException(String description) {
        super(description);
    }
}

package com.pacific.volcano.campsitereservations.exception;

public class CampsiteUnavailableException
                extends RuntimeException {
    public CampsiteUnavailableException(String description, Exception exc) {
        super(description,exc);
    }

    public CampsiteUnavailableException(String description) {
        super(description);
    }
}

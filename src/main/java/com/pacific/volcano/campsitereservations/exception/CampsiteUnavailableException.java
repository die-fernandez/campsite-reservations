package com.pacific.volcano.campsitereservations.exception;

public class CampsiteUnavailableException
                extends RuntimeException {
    private static final long serialVersionUID = 2100275753007504979L;

    public CampsiteUnavailableException(String description, Exception exc) {
        super(description,exc);
    }

    public CampsiteUnavailableException(String description) {
        super(description);
    }
}

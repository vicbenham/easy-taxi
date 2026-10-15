package org.easygroup.easytaxi.exceptions;

public class GoogleMapsException extends RuntimeException {
    private final String status;

    public GoogleMapsException(String status, String message) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}

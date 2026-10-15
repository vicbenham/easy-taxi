package org.easygroup.easytaxi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class EstimateExpiredException extends RuntimeException {

    public EstimateExpiredException() {
        super("Price estimate token has expired");
    }
}

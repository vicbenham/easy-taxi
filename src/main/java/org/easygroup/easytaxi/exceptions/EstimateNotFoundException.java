package org.easygroup.easytaxi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EstimateNotFoundException extends RuntimeException {

    public EstimateNotFoundException() {
        super("Price estimate token was not found");
    }
}

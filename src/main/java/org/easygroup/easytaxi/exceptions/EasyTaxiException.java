package org.easygroup.easytaxi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EasyTaxiException extends RuntimeException {

    public EasyTaxiException(String message) {
        super(message);
    }
}

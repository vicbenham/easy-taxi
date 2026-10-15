package org.easygroup.easytaxi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EstimateAlreadyUsedException extends RuntimeException {

    public EstimateAlreadyUsedException() {
        super("Price estimate token has already been used");
    }
}

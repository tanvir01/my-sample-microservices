package com.tanservices.shipment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateShipmentException extends RuntimeException {
    public DuplicateShipmentException(String message) {
        super(message);
    }
}

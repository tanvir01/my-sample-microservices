package com.tanservices.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStatusUpdateException extends RuntimeException {
    public InvalidStatusUpdateException(String message) {
        super(message);
    }
}

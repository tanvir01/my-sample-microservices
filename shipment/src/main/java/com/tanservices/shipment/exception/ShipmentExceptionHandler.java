package com.tanservices.shipment.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ShipmentExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ShipmentErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "HttpMessageNotReadableException: " + ex.getMessage();
        ShipmentErrorResponse shipmentErrorResponse = new ShipmentErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.badRequest().body(shipmentErrorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ShipmentErrorResponse> handleNullPointerException(NullPointerException ex) {
        String errorMessage = "NullPointerException: " + ex.getMessage();
        ShipmentErrorResponse shipmentErrorResponse = new ShipmentErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.badRequest().body(shipmentErrorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ShipmentErrorResponse> handleDuplicateKeyException(DataIntegrityViolationException ex) {
        String errorMessage = "DataIntegrityViolationException: " + "Duplicate key value violates unique constraint - " + ex.getMessage();
        ShipmentErrorResponse shipmentErrorResponse = new ShipmentErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(shipmentErrorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ShipmentErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        // TODO: improve error message. Simplify the message to just use correct data type and size ( for trackingCode)
        String errorMessage = "ConstraintViolationException: " + "Constraint Violation Error - " + ex.getMessage();
        ShipmentErrorResponse shipmentErrorResponse = new ShipmentErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(shipmentErrorResponse);
    }

    @ExceptionHandler(ShipmentNotFoundException.class)
    public ResponseEntity<ShipmentErrorResponse> handleShipmentNotFoundException(ShipmentNotFoundException ex) {
        String errorMessage = "ShipmentNotFoundException: " + ex.getMessage();
        ShipmentErrorResponse shipmentErrorResponse = new ShipmentErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(shipmentErrorResponse);
    }

    @ExceptionHandler(DuplicateShipmentException.class)
    public ResponseEntity<ShipmentErrorResponse> handleDuplicateShipmentException(DuplicateShipmentException ex) {
        String errorMessage = "DuplicateShipmentException: " + ex.getMessage();
        ShipmentErrorResponse shipmentErrorResponse = new ShipmentErrorResponse(HttpStatus.CONFLICT.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(shipmentErrorResponse);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ShipmentErrorResponse> handleFeignException(FeignException ex) {
        String errorMessage = "FeignException: " + ex.getMessage();
        ShipmentErrorResponse shipmentErrorResponse = new ShipmentErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(shipmentErrorResponse);
    }
}

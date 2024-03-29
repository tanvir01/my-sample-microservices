package com.tanservices.order.exception;

import feign.FeignException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<OrderErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = "MethodArgumentNotValidException: " + ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        OrderErrorResponse orderErrorResponse = new OrderErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.badRequest().body(orderErrorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<OrderErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "HttpMessageNotReadableException: " + ex.getMessage();
        OrderErrorResponse orderErrorResponse = new OrderErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.badRequest().body(orderErrorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<OrderErrorResponse> handleNullPointerException(NullPointerException ex) {
        String errorMessage = "NullPointerException: " + ex.getMessage();
        OrderErrorResponse orderErrorResponse = new OrderErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.badRequest().body(orderErrorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<OrderErrorResponse> handleDuplicateKeyException(DataIntegrityViolationException ex) {
        String errorMessage = "DataIntegrityViolationException: " + "Duplicate key value violates unique constraint: " + ex.getMessage();
        OrderErrorResponse orderErrorResponse = new OrderErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(orderErrorResponse);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<OrderErrorResponse> handleShipmentNotFoundException(OrderNotFoundException ex) {
        String errorMessage = "OrderNotFoundException: " + ex.getMessage();
        OrderErrorResponse orderErrorResponse = new OrderErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderErrorResponse);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<OrderErrorResponse> handleFeignException(FeignException ex) {
        String errorMessage = "FeignException: " + ex.getMessage();
        OrderErrorResponse shipmentErrorResponse = new OrderErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(shipmentErrorResponse);
    }

    @ExceptionHandler(InvalidStatusUpdateException.class)
    public ResponseEntity<OrderErrorResponse> handleFeignException(InvalidStatusUpdateException ex) {
        String errorMessage = "InvalidStatusUpdateException: " + ex.getMessage();
        OrderErrorResponse shipmentErrorResponse = new OrderErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(shipmentErrorResponse);
    }
}

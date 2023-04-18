package com.tanservices.myauthprovider.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthProviderExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<AuthProviderErrorResponse> handleUsernameNotFoundExceptio(UsernameNotFoundException ex) {
        String errorMessage = "UsernameNotFoundException: " + ex.getMessage();
        AuthProviderErrorResponse errorResponse = new AuthProviderErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthProviderErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        String errorMessage = "BadCredentialsException: " + ex.getMessage();
        AuthProviderErrorResponse errorResponse = new AuthProviderErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}

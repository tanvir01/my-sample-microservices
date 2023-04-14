package com.tanservices.myauthprovider.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthProviderErrorResponse {
    private int status;
    private String message;
}

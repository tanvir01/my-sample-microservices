package com.tanservices.order.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderErrorResponse {
    private int status;
    private String message;
}

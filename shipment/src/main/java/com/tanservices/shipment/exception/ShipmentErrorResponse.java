package com.tanservices.shipment.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShipmentErrorResponse {
    private int status;
    private String message;
}

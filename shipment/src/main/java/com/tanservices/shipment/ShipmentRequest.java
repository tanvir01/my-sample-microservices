package com.tanservices.shipment;

public record ShipmentRequest(
        Long orderId,
        String trackingCode) {
}

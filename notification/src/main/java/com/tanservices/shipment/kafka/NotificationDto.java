package com.tanservices.shipment.kafka;

public record NotificationDto(
        Long orderId,
        Long shipmentId,
        String customerEmail,
        String message) {
}
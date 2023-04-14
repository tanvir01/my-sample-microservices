package com.tanservices.shipment.kafka;

public record NotificationDto(
        Long orderId,
        Long shipmentId,
        Long userId,
        String message) {
}

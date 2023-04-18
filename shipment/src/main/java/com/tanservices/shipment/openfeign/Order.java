package com.tanservices.shipment.openfeign;

public record Order(
        Long id,
        Long userId,
        Double totalAmount,
        OrderStatus status) {
}

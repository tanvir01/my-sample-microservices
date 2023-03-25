package com.tanservices.shipment.openfeign;

public record Order(
        Long id,
        String customerName,
        String customerEmail,
        Double totalAmount,
        OrderStatus status) {
}

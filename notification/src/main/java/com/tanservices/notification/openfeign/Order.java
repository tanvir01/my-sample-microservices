package com.tanservices.notification.openfeign;

public record Order(
        Long id,
        String customerName,
        String customerEmail,
        Double totalAmount,
        OrderStatus status) {
}

package com.tanservices.notification.openfeign;

public record User(
        Long id,
        String name,
        String email,
        String address) {
}

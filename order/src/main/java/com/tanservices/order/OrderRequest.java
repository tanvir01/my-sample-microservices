package com.tanservices.order;

public record OrderRequest(
        String customerName,
        String customerEmail,
        Double totalAmount) {
}

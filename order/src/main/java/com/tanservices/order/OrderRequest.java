package com.tanservices.order;

public record OrderRequest(
        String customerName,
        String customerEmail,
        String shippingAddress,
        Double totalAmount) {
}

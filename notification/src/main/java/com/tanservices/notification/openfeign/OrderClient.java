package com.tanservices.notification.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "order", url = "${order-service.url}")
public interface OrderClient {

    @GetMapping("/{orderId}")
    Order getOrderById(@PathVariable("orderId") Long id);
}

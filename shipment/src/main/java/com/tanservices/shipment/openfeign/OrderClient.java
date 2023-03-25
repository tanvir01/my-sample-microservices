package com.tanservices.shipment.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "order", url = "${order-service.url}")
public interface OrderClient {

    @GetMapping("/{orderId}")
    Order getOrderById(@PathVariable("orderId") Long id);
    @RequestMapping(method = RequestMethod.PATCH, value = "/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderStatusRequest orderStatusRequest);
}

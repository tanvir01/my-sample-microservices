package com.tanservices.shipment.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "order", url = "${order-service.url}")
public interface OrderClient {
    @RequestMapping(method = RequestMethod.PATCH, value = "/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderStatusRequest orderStatusRequest);
}

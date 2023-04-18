package com.tanservices.order.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "shipment", url = "${shipment-service.url}", configuration = FeignConfiguration.class)
public interface ShipmentClient {

    @RequestMapping(method = RequestMethod.POST, value = "/{orderId}/cancel-by-order")
    void markShipmentCancelled(@PathVariable("orderId") Long orderId);
}

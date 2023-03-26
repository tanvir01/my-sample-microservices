package com.tanservices.shipment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return new ResponseEntity<>(shipmentService.getAllShipments(),
                                    HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id).get());
    }

    @PostMapping
    public ResponseEntity<?> createShipment(@RequestBody ShipmentRequest shipmentRequest) {
        Shipment newShipment = shipmentService.createShipment(shipmentRequest);
        return new ResponseEntity<>(newShipment, HttpStatus.CREATED);
    }

    @PostMapping("/{orderId}/cancel-by-order")
    public ResponseEntity<Void> cancelShipmentByOrderId(@PathVariable Long orderId) {
        shipmentService.markShipmentCancelledByOrderId(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Long id, @RequestBody ShipmentRequest shipmentRequest) {
        return ResponseEntity.ok(shipmentService.updateShipment(id, shipmentRequest));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateShipmentStatus(@PathVariable Long id, @RequestBody ShipmentStatusRequest shipmentStatusRequest) {
        log.info(String.valueOf(shipmentStatusRequest));
        shipmentService.updateShipmentStatus(id, shipmentStatusRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

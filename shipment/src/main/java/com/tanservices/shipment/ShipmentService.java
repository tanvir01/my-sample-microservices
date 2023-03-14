package com.tanservices.shipment;

import com.tanservices.shipment.exception.DuplicateShipmentException;
import com.tanservices.shipment.exception.ShipmentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> getShipmentById(Long id) {
        return shipmentRepository.findById(id);
    }

    public Shipment createShipment(ShipmentRequest shipmentRequest) {
        if (shipmentRepository.existsByOrderId(shipmentRequest.orderId())) {
            throw new DuplicateShipmentException("Shipment with orderId " + shipmentRequest.orderId() + " already exists");
        }

        if (shipmentRepository.existsByTrackingCode(shipmentRequest.trackingCode())) {
            throw new DuplicateShipmentException("Shipment with trackingCode " + shipmentRequest.trackingCode() + " already exists");
        }

        Shipment shipment = Shipment.builder()
                .orderId(shipmentRequest.orderId())
                .address(shipmentRequest.address())
                .trackingCode(shipmentRequest.trackingCode())
                .status(Shipment.ShipmentStatus.NEW)
                .build();

        return shipmentRepository.save(shipment);
    }

    public Shipment updateShipment(Shipment updatedShipment, ShipmentRequest shipmentRequest) {
        // Check if the shipment exists with the given shipmentId
        Shipment existingShipment = shipmentRepository.findById(updatedShipment.getId())
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + updatedShipment.getId()));

        // Check if the orderId has been updated in the request body
        Long orderId = shipmentRequest.orderId();
        if (orderId != null && !orderId.equals(existingShipment.getOrderId())) {
            // Check if there is already a shipment with the same orderId
            if (shipmentRepository.existsByOrderId(orderId)) {
                throw new DuplicateShipmentException("Shipment with orderId " + orderId + " already exists");
            }
            existingShipment.setOrderId(orderId);
        }

        // Check if the trackingCode has been updated in the request body
        String trackingCode = shipmentRequest.trackingCode();
        if (trackingCode != null && !trackingCode.equals(existingShipment.getTrackingCode())) {
            // Check if there is already a shipment with the same trackingCode
            if (shipmentRepository.existsByTrackingCode(trackingCode)) {
                throw new DuplicateShipmentException("Shipment with trackingCode " + trackingCode + " already exists");
            }
            existingShipment.setTrackingCode(trackingCode);
        }

        // Update the shipment
        updatedShipment.setOrderId(shipmentRequest.orderId());
        updatedShipment.setAddress(shipmentRequest.address());
        updatedShipment.setTrackingCode(shipmentRequest.trackingCode());
        shipmentRepository.save(updatedShipment);

        return updatedShipment;
    }

    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }
}
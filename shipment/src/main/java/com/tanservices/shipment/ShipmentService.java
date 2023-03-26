package com.tanservices.shipment;

import com.tanservices.shipment.exception.DuplicateShipmentException;
import com.tanservices.shipment.exception.InvalidStatusUpdateException;
import com.tanservices.shipment.exception.OrderNotFoundException;
import com.tanservices.shipment.exception.ShipmentNotFoundException;
import com.tanservices.shipment.openfeign.Order;
import com.tanservices.shipment.openfeign.OrderClient;
import com.tanservices.shipment.openfeign.OrderStatus;
import com.tanservices.shipment.openfeign.OrderStatusRequest;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderClient orderClient;

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository, OrderClient orderClient) {
        this.shipmentRepository = shipmentRepository;
        this.orderClient = orderClient;
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> getShipmentById(Long id) {
        return Optional.ofNullable(shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id)));
    }

    public Shipment createShipment(ShipmentRequest shipmentRequest) {
        if (shipmentRepository.existsByOrderId(shipmentRequest.orderId())) {
            throw new DuplicateShipmentException("Shipment with orderId " + shipmentRequest.orderId() + " already exists");
        }

        if (shipmentRepository.existsByTrackingCode(shipmentRequest.trackingCode())) {
            throw new DuplicateShipmentException("Shipment with trackingCode " + shipmentRequest.trackingCode() + " already exists");
        }

        //check if order exists
        Optional<Order> order = getOrderById(shipmentRequest.orderId());
        if (order.isEmpty()){
            throw new OrderNotFoundException(shipmentRequest.orderId());
        }

        // build shipment
        Shipment shipment = Shipment.builder()
                .orderId(order.get().id())
                .address(shipmentRequest.address())
                .trackingCode(shipmentRequest.trackingCode())
                .status(Shipment.ShipmentStatus.NEW)
                .build();

        //update order status
        updateOrderStatus(shipmentRequest.orderId(), OrderStatus.PROCESSING);

        return shipmentRepository.save(shipment);
    }

    public Shipment updateShipment(Long id, ShipmentRequest shipmentRequest) {
        // Check if the shipment exists with the given shipmentId
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        // Check if the orderId has been updated in the request body
        Long orderId = shipmentRequest.orderId();
        if (orderId != null && !orderId.equals(existingShipment.getOrderId())) {
            // Check if there is already a shipment with the same orderId
            if (shipmentRepository.existsByOrderId(orderId)) {
                throw new DuplicateShipmentException("Shipment with orderId " + orderId + " already exists");
            }

            //check if order exists
            Optional<Order> order = getOrderById(shipmentRequest.orderId());
            if (order.isEmpty()) {
                throw new OrderNotFoundException(shipmentRequest.orderId());
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
        existingShipment.setAddress(shipmentRequest.address());
        shipmentRepository.save(existingShipment);

        return existingShipment;
    }

    public void deleteShipment(Long id) {
        // Check if the shipment exists with the given shipmentId
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        shipmentRepository.delete(existingShipment);
    }

    public void markShipmentCancelledByOrderId(Long orderId) {
        Shipment existingShipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("There is no shipment with orderId: " + orderId));

        existingShipment.setStatus(Shipment.ShipmentStatus.CANCELLED);
        shipmentRepository.save(existingShipment);
    }

    public void updateShipmentStatus(Long id, ShipmentStatusRequest shipmentStatusRequest) {
        // Do not allow shipment status to updated as Cancelled by customer
        if (shipmentStatusRequest.status() == Shipment.ShipmentStatus.CANCELLED) {
            throw new InvalidStatusUpdateException("You cannot update shipment status to be CANCELLED directly." +
                                                    "The order needs to be CANCELLED.");
        }

        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        existingShipment.setStatus(shipmentStatusRequest.status());

        //if shipment completed, mark order completed
        if(existingShipment.getStatus() == Shipment.ShipmentStatus.COMPLETED) {
            updateOrderStatus(existingShipment.getOrderId(), OrderStatus.COMPLETED);
        }

        shipmentRepository.save(existingShipment);
    }

    private void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest(orderStatus);
        orderClient.updateOrderStatus(orderId, orderStatusRequest);
    }

    private Optional<Order> getOrderById(Long orderId) {
        try {
            Order order = orderClient.getOrderById(orderId);
            return Optional.of(order);
        } catch (FeignException ex) {
            if (ex.status() == 404) {
                return Optional.empty();
            } else {
                throw ex;
            }
        }
    }
}

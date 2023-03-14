package com.tanservices.shipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingCode(String trackingCode);
    Optional<Shipment> findByOrderId(Long orderId);
    Optional<Shipment> findByOrderIdOrTrackingCode(Long orderId, String trackingCode);

    boolean existsByOrderId(Long orderId);

    boolean existsByTrackingCode(String trackingCode);
}

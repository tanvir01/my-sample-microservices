package com.tanservices.shipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByIdAndUserId(Long id, Long userId);

    List<Shipment> findByUserId(Long userId);

    boolean existsByOrderId(Long orderId);

    boolean existsByTrackingCode(String trackingCode);

    Optional<Shipment> findByOrderIdAndUserId(Long orderId, Long userId);
}

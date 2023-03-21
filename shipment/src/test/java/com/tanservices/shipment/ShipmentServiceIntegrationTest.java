package com.tanservices.shipment;
import com.tanservices.shipment.exception.DuplicateShipmentException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class ShipmentServiceIntegrationTest {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    public void testGetAllShipments() {
        assertThat(shipmentService.getAllShipments()).isEmpty();
    }

    @Test
    @Transactional
    public void testGetShipmentById() {
        // given
        Shipment shipment = Shipment.builder()
                .orderId(1L)
                .address("123 Main St")
                .trackingCode("ABC123XYZ786")
                .status(Shipment.ShipmentStatus.NEW)
                .build();
        shipmentRepository.save(shipment);

        // when
        Optional<Shipment> foundShipment = shipmentService.getShipmentById(shipment.getId());

        // then
        assertThat(foundShipment.isPresent()).isTrue();
        assertThat(foundShipment.get()).isEqualTo(shipment);
    }

    @Test
    @Transactional
    public void testCreateShipment() {
        // given
        ShipmentRequest shipmentRequest = new ShipmentRequest(1L, "123 Main St", "ABC123XYZ786");

        // when
        Shipment shipment = shipmentService.createShipment(shipmentRequest);

        // then
        assertThat(shipment).isNotNull();
        assertThat(shipment.getOrderId()).isEqualTo(shipmentRequest.orderId());
        assertThat(shipment.getAddress()).isEqualTo(shipmentRequest.address());
        assertThat(shipment.getTrackingCode()).isEqualTo(shipmentRequest.trackingCode());
        assertThat(shipment.getStatus()).isEqualTo(Shipment.ShipmentStatus.NEW);
    }

    @Test
    @Transactional
    public void testCreateShipmentWithDuplicateOrderId() {
        // given
        Shipment existingShipment = Shipment.builder()
                .orderId(1L)
                .address("123 Main St")
                .trackingCode("ABC123XYZ786")
                .status(Shipment.ShipmentStatus.NEW)
                .build();
        shipmentRepository.save(existingShipment);

        ShipmentRequest shipmentRequest = new ShipmentRequest(existingShipment.getOrderId(), "456 Elm St", "DEF456");

        // when/then
        assertThatThrownBy(() -> shipmentService.createShipment(shipmentRequest))
                .isInstanceOf(DuplicateShipmentException.class)
                .hasMessageContaining("orderId " + existingShipment.getOrderId() + " already exists");
    }

    @Test
    @Transactional
    public void testCreateShipmentWithDuplicateTrackingCode() {
        // given
        Shipment existingShipment = Shipment.builder()
                .orderId(1L)
                .address("123 Main St")
                .trackingCode("ABC123XYZ786")
                .status(Shipment.ShipmentStatus.NEW)
                .build();
        shipmentRepository.save(existingShipment);

        ShipmentRequest shipmentRequest = new ShipmentRequest(2L, "456 Elm St", existingShipment.getTrackingCode());

        // when/then
        assertThatThrownBy(() -> shipmentService.createShipment(shipmentRequest))
                .isInstanceOf(DuplicateShipmentException.class)
                .hasMessageContaining("trackingCode " + existingShipment.getTrackingCode() + " already exists");
    }

    @Test
    @Transactional
    public void testUpdateShipment() {
        // given
        Shipment existingShipment = Shipment.builder()
                .orderId(1L)
                .address("123 Main St")
                .trackingCode("ABC123XYZ786")
                .status(Shipment.ShipmentStatus.NEW)
                .build();
        shipmentRepository.save(existingShipment);

        Long id = existingShipment.getId();
        ShipmentRequest shipmentRequest = new ShipmentRequest(2L, "456 Elm St", "DEF456XYZ786");

        // when
        Shipment updatedShipment = shipmentService.updateShipment(id, shipmentRequest);

        // then
        assertThat(updatedShipment).isNotNull();
        assertThat(updatedShipment.getId()).isEqualTo(id);
        assertThat(updatedShipment.getOrderId()).isEqualTo(shipmentRequest.orderId());
        assertThat(updatedShipment.getAddress()).isEqualTo(shipmentRequest.address());
        assertThat(updatedShipment.getTrackingCode()).isEqualTo(shipmentRequest.trackingCode());
    }

    @Test
    @Transactional
    public void testDeleteShipment() {
        // given
        Shipment existingShipment = Shipment.builder()
                .orderId(1L)
                .address("123 Main St")
                .trackingCode("ABC123XYZ786")
                .status(Shipment.ShipmentStatus.NEW)
                .build();
        shipmentRepository.save(existingShipment);

        //when
        shipmentService.deleteShipment(existingShipment.getId());

        //then
        Optional<Shipment> shipment = shipmentRepository.findById(existingShipment.getId());
        assertThat(shipment).isEmpty();
    }

}

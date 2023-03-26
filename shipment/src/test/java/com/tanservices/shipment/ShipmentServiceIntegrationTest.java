package com.tanservices.shipment;

import com.tanservices.shipment.exception.DuplicateShipmentException;
import com.tanservices.shipment.exception.ShipmentNotFoundException;
import com.tanservices.shipment.kafka.NotificationDto;
import com.tanservices.shipment.openfeign.Order;
import com.tanservices.shipment.openfeign.OrderClient;
import com.tanservices.shipment.openfeign.OrderStatus;
import com.tanservices.shipment.openfeign.OrderStatusRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ShipmentServiceIntegrationTest {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @MockBean
    private OrderClient orderClient;

    @MockBean
    private KafkaTemplate<String, NotificationDto> kafkaTemplate;



    @Test
    public void testGetAllShipments() {
        assertThat(shipmentService.getAllShipments()).isEmpty();
    }

    @Test
    public void testGetShipmentByIdNotFound() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThrows(ShipmentNotFoundException.class, () -> {
            shipmentService.getShipmentById(nonExistentId);
        });
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
        Long orderId = 1L;
        String address = "123 Main St";
        String trackingCode = "123456sample101";

        Order order = new Order(orderId, "John Doe", "john@gmail.com", 223.0, OrderStatus.PENDING);
        when(orderClient.getOrderById(orderId)).thenReturn(order);

        // when
        ShipmentRequest request = new ShipmentRequest(orderId, address, trackingCode);
        Shipment shipment = shipmentService.createShipment(request);

        // then
        assertThat(shipment.getOrderId()).isEqualTo(orderId);
        assertThat(shipment.getTrackingCode()).isEqualTo(trackingCode);
        assertThat(shipment.getAddress()).isEqualTo(address);
        assertThat(shipment.getStatus()).isEqualTo(Shipment.ShipmentStatus.NEW);

        OrderStatusRequest orderStatusRequest = new OrderStatusRequest(OrderStatus.PROCESSING);
        verify(orderClient).updateOrderStatus(orderId, orderStatusRequest);
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

        Long orderId = 2L;
        Order order = new Order(orderId, "John Doe", "john@gmail.com", 223.0, OrderStatus.PENDING);
        when(orderClient.getOrderById(orderId)).thenReturn(order);

        Long id = existingShipment.getId();
        ShipmentRequest shipmentRequest = new ShipmentRequest(orderId, "456 Elm St", "DEF456XYZ786");

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
    public void testUpdateShipmentStatus() {
        // given
        Shipment existingShipment = Shipment.builder()
                .orderId(1L)
                .address("123 Main St")
                .trackingCode("ABC123XYZ786")
                .status(Shipment.ShipmentStatus.NEW)
                .build();
        shipmentRepository.save(existingShipment);

        // when
        ShipmentStatusRequest request = new ShipmentStatusRequest(Shipment.ShipmentStatus.COMPLETED);
        shipmentService.updateShipmentStatus(existingShipment.getId(), request);

        // then
        Shipment updatedShipment = (shipmentRepository.findById(existingShipment.getId())).get();
        assertThat(updatedShipment.getStatus()).isEqualTo(Shipment.ShipmentStatus.COMPLETED);
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

    @Test
    @Transactional
    public void testMarkShipmentCancelledByOrderId() {
        // given
        Long orderId = 1L;
        Shipment existingShipment = Shipment.builder()
                .orderId(orderId)
                .address("123 Main St")
                .trackingCode("ABC123XYZ786")
                .status(Shipment.ShipmentStatus.NEW)
                .build();
        shipmentRepository.save(existingShipment);

        Shipment updatedShipment = (shipmentRepository.findById(existingShipment.getId())).get();
        assertThat(updatedShipment.getStatus()).isEqualTo(Shipment.ShipmentStatus.NEW);

        // when
        shipmentService.markShipmentCancelledByOrderId(orderId);

        // then
        updatedShipment = (shipmentRepository.findById(existingShipment.getId())).get();
        assertThat(updatedShipment.getStatus()).isEqualTo(Shipment.ShipmentStatus.CANCELLED);
    }
}

package com.tanservices.order;

import com.tanservices.order.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStateMachineService orderStateMachineService;

    @Test
    @Transactional
    public void testGetAllOrders() {
        // given
        Order order1 = createDummyOrder();

        Order order2 = new Order();
        order2.setCustomerName("Jane Doe");
        order2.setCustomerEmail("jane.doe@example.com");
        order2.setTotalAmount(223.10);
        order2.setStatus(OrderStatus.PENDING);
        orderRepository.save(order2);

        // when
        List<Order> allOrders = orderService.getAllOrders();

        // then
        assertThat(allOrders).hasSize(2);
    }

    @Test
    @Transactional
    public void testGetOrderById() {
        // given
        Order order = createDummyOrder();

        // when
        Optional<Order> foundOrder = orderService.getOrderById(order.getId());

        // then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(order.getId());
    }

    @Test
    public void testGetOrderByIdNotFound() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(nonExistentId);
        });
    }

    @Test
    @Transactional
    public void testCreateOrder() {
        // given
        OrderRequest orderRequest = new OrderRequest("John Doe", "john.doe@example.com", 100.00);

        // when
        Order createdOrder = orderService.createOrder(orderRequest);

        // then
        assertThat(createdOrder.getId()).isNotNull();
    }

    @Test
    @Transactional
    public void testUpdateOrder() {
        // given
        Order existingOrder = createDummyOrder();

        Long id = existingOrder.getId();
        OrderRequest orderRequest = new OrderRequest("Jane Doe", "jane.doe@example.com", 200.00);

        // when
        Order updatedOrder = orderService.updateOrder(id, orderRequest);

        // then
        assertThat(updatedOrder.getCustomerName()).isEqualTo(orderRequest.customerName());
        assertThat(updatedOrder.getCustomerEmail()).isEqualTo(orderRequest.customerEmail());
        assertThat(updatedOrder.getTotalAmount()).isEqualTo(orderRequest.totalAmount());
    }

    @Test
    @Transactional
    public void testUpdateOrderStatus() {
        // given
        Order order = createDummyOrder();

        OrderStatusRequest orderStatusRequest = new OrderStatusRequest(OrderStatus.PROCESSING);

        // when
        orderService.updateOrderStatus(order.getId(), orderStatusRequest);

        // then
        order = orderRepository.findById(order.getId()).get();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    @Transactional
    public void testDeleteOrder() {
        // given
        Order existingOrder = createDummyOrder();

        Long orderId = existingOrder.getId();

        // when
        orderService.deleteOrder(orderId);

        // then
        Optional<Order> deleted = orderRepository.findById(orderId);
        assertThat(deleted).isEmpty();
    }

    @Test
    @Transactional
    public void markOrderCompletedTest() {
        // given
        Order existingOrder = createDummyOrder();
        orderStateMachineService.processOrderState(existingOrder, OrderStateMachine.OrderEvent.PROCESS);
        orderRepository.save(existingOrder);
        existingOrder = orderRepository.findById(existingOrder.getId()).get();

        assertEquals(OrderStatus.PROCESSING, existingOrder.getStatus());

        // when
        orderService.markOrderCompleted(existingOrder.getId());

        // retrieve the updated order from the test database and assert that its status has been updated to COMPLETED
        Order updatedOrder = orderRepository.findById(existingOrder.getId()).get();

        //then
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
    }

    private Order createDummyOrder() {
        Order existingOrder = new Order();
        existingOrder.setCustomerName("John Doe");
        existingOrder.setCustomerEmail("john.doe@example.com");
        existingOrder.setTotalAmount(100.00);
        existingOrder.setStatus(OrderStatus.PENDING);
        existingOrder = orderRepository.save(existingOrder);

        return existingOrder;
    }
}

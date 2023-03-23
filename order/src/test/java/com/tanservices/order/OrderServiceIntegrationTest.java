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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    public void testGetAllOrders() {
        // given
        Order order1 = new Order();
        order1.setCustomerName("John Doe");
        order1.setCustomerEmail("john.doe@example.com");
        order1.setTotalAmount(110.30);
        order1.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setCustomerName("Jane Doe");
        order2.setCustomerEmail("jane.doe@example.com");
        order2.setTotalAmount(223.10);
        order2.setStatus(Order.OrderStatus.PENDING);
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
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setCustomerEmail("john.doe@example.com");
        order.setTotalAmount(100.00);
        order.setStatus(Order.OrderStatus.PENDING);
        order = orderRepository.save(order);

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
        Order existingOrder = new Order();
        existingOrder.setCustomerName("John Doe");
        existingOrder.setCustomerEmail("john.doe@example.com");
        existingOrder.setTotalAmount(100.00);
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        existingOrder = orderRepository.save(existingOrder);

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
    public void testDeleteOrder() {
        // given
        Order existingOrder = new Order();
        existingOrder.setCustomerName("John Doe");
        existingOrder.setCustomerEmail("john.doe@example.com");
        existingOrder.setTotalAmount(100.00);
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        existingOrder = orderRepository.save(existingOrder);

        Long orderId = existingOrder.getId();

        // when
        orderService.deleteOrder(orderId);

        // then
        Optional<Order> deleted = orderRepository.findById(orderId);
        assertThat(deleted).isEmpty();
    }
}

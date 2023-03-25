package com.tanservices.order;

import com.tanservices.order.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id)));
    }

    public Order createOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .customerName(orderRequest.customerName())
                .customerEmail(orderRequest.customerEmail())
                .totalAmount(orderRequest.totalAmount())
                .status(Order.OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, OrderRequest orderRequest) {
        // Check if the shipment exists with the given shipmentId
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // Update the order
        existingOrder.setCustomerName(orderRequest.customerName());
        existingOrder.setCustomerEmail(orderRequest.customerEmail());
        existingOrder.setTotalAmount(orderRequest.totalAmount());
        orderRepository.save(existingOrder);

        return existingOrder;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public void updateOrderStatus(Long id, OrderStatusRequest orderStatusRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(orderStatusRequest.status());
        orderRepository.save(order);
    }
}


package com.tanservices.order;

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
        return orderRepository.findById(id);
    }

    public Order createOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .customerName(orderRequest.customerName())
                .customerEmail(orderRequest.customerEmail())
                .shippingAddress(orderRequest.shippingAddress())
                .totalAmount(orderRequest.totalAmount())
                .status(Order.OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }

    public Order updateOrder(Optional<Order> existingOrder, OrderRequest orderRequest) {
        // Update the order
        Order updatedOrder = existingOrder.get();
        updatedOrder.setCustomerName(orderRequest.customerName());
        updatedOrder.setCustomerEmail(orderRequest.customerEmail());
        updatedOrder.setShippingAddress(orderRequest.shippingAddress());
        updatedOrder.setTotalAmount(orderRequest.totalAmount());
        orderRepository.save(updatedOrder);

        return updatedOrder;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}


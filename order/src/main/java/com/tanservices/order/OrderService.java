package com.tanservices.order;

import com.tanservices.order.exception.InvalidStatusUpdateException;
import com.tanservices.order.exception.OrderNotFoundException;
import com.tanservices.order.openfeign.ShipmentClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final ShipmentClient shipmentClient;

    private final OrderStateMachineService orderStateMachineService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ShipmentClient shipmentClient, OrderStateMachineService orderStateMachineService) {
        this.orderRepository = orderRepository;
        this.shipmentClient = shipmentClient;
        this.orderStateMachineService = orderStateMachineService;
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
                .status(OrderStatus.PENDING)
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
        // Do not allow order status to updated as COMPLETED by customer
        if (orderStatusRequest.status() == OrderStatus.COMPLETED) {
            throw new InvalidStatusUpdateException("You cannot update order status to be COMPLETED directly." +
                    "The shipment needs to be COMPLETED.");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        log.info("Status Requested for orderId " + order.getId() + " is " + orderStatusRequest.status());

        // Send event to state machine based on requested status update
        switch (orderStatusRequest.status()) {
            case PROCESSING:
                orderStateMachineService.processOrderState(order, OrderStateMachine.OrderEvent.PROCESS);
                break;
            case CANCELLED:
                orderStateMachineService.processOrderState(order, OrderStateMachine.OrderEvent.CANCEL);
                cancelShipment(id);
                break;
        }

        orderRepository.save(order);
    }

    private void cancelShipment(Long orderId) {
        try {
            shipmentClient.markShipmentCancelled(orderId);
        } catch (FeignException ex) {
            if (ex.status() == 404) {
                log.info("There is no shipment for orderId: " + orderId);
            } else {
                throw ex;
            }
        }
    }

    public void markOrderCompleted(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        orderStateMachineService.processOrderState(order, OrderStateMachine.OrderEvent.COMPLETE);
        orderRepository.save(order);
    }
}


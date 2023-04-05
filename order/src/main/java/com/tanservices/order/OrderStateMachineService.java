package com.tanservices.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderStateMachineService {

    private final StateMachine<OrderStatus, OrderStateMachine.OrderEvent> stateMachine;

    public OrderStateMachineService() throws Exception {
        this.stateMachine = OrderStateMachine.buildMachine();
        this.stateMachine.start();
    }

    public StateMachine<OrderStatus, OrderStateMachine.OrderEvent> getStateMachine() {
        return stateMachine;
    }

    public void processOrderState(Order order, OrderStateMachine.OrderEvent orderEvent) {
        // Reset the state machine to the current state of the order
        resetStateMachine(order.getStatus());

        stateMachine.sendEvent(orderEvent);
        OrderStatus currentState = stateMachine.getState().getId();

        // Update order status based on current state of state machine
        order.setStatus(currentState);

        log.info("Current state for order id " + order.getId() + " is " + currentState);
    }

    private void resetStateMachine(OrderStatus orderStatus) {
        this.stateMachine.stop();
        this.stateMachine.getStateMachineAccessor().doWithAllRegions(accessor -> accessor.resetStateMachine(new DefaultStateMachineContext<>(orderStatus, null, null, null)));
        this.stateMachine.start();
    }
}

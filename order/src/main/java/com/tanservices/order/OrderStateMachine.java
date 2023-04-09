package com.tanservices.order;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;

import java.util.EnumSet;

public class OrderStateMachine {

    public enum OrderEvent {
        PROCESS, COMPLETE, CANCEL
    }

    public static StateMachine<OrderStatus, OrderEvent> buildMachine() throws Exception {
        StateMachineBuilder.Builder<OrderStatus, OrderEvent> builder = StateMachineBuilder.builder();

        builder.configureStates()
                .withStates()
                .initial(OrderStatus.PENDING)
                .states(EnumSet.allOf(OrderStatus.class));

        builder.configureTransitions()
                .withExternal()
                .source(OrderStatus.PENDING).target(OrderStatus.PROCESSING)
                .event(OrderEvent.PROCESS)
                .and()
                .withExternal()
                .source(OrderStatus.PROCESSING).target(OrderStatus.COMPLETED)
                .event(OrderEvent.COMPLETE)
                .and()
                .withExternal()
                .source(OrderStatus.PROCESSING).target(OrderStatus.CANCELLED)
                .event(OrderEvent.CANCEL)
                .and()
                .withExternal()
                .source(OrderStatus.PENDING).target(OrderStatus.CANCELLED)
                .event(OrderEvent.CANCEL);

        return builder.build();
    }
}
package com.tanservices.shipment;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;

import java.util.EnumSet;

public class ShipmentStateMachine {

    public enum ShipmentEvent {
        COMPLETE, LOSE, CANCEL
    }

    public static StateMachine<ShipmentStatus, ShipmentEvent> buildMachine() throws Exception {
        StateMachineBuilder.Builder<ShipmentStatus, ShipmentEvent> builder = StateMachineBuilder.builder();

        builder.configureStates()
                .withStates()
                .initial(ShipmentStatus.NEW)
                .states(EnumSet.allOf(ShipmentStatus.class));

        builder.configureTransitions()
                .withExternal()
                .source(ShipmentStatus.NEW).target(ShipmentStatus.COMPLETED)
                .event(ShipmentEvent.COMPLETE)
                .and()
                .withExternal()
                .source(ShipmentStatus.NEW).target(ShipmentStatus.LOST)
                .event(ShipmentEvent.LOSE)
                .and()
                .withExternal()
                .source(ShipmentStatus.NEW).target(ShipmentStatus.CANCELLED)
                .event(ShipmentEvent.CANCEL);

        return builder.build();
    }
}
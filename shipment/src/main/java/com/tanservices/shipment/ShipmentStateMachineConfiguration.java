package com.tanservices.shipment;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;


@Configuration
@EnableStateMachine
public class ShipmentStateMachineConfiguration extends StateMachineConfigurerAdapter<ShipmentStatus, String> {

    @Override
    public void configure(StateMachineStateConfigurer<ShipmentStatus, String> states) throws Exception {
        states.withStates()
                .initial(ShipmentStatus.NEW)
                .end(ShipmentStatus.COMPLETED)
                .end(ShipmentStatus.LOST)
                .end(ShipmentStatus.CANCELLED)
                .states(EnumSet.allOf(ShipmentStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ShipmentStatus, String> transitions) throws Exception {
        transitions.withExternal()
                .source(ShipmentStatus.NEW).target(ShipmentStatus.COMPLETED).event("complete")
                .and()
                .withExternal()
                .source(ShipmentStatus.NEW).target(ShipmentStatus.LOST).event("lose")
                .and()
                .withExternal()
                .source(ShipmentStatus.NEW).target(ShipmentStatus.CANCELLED).event("cancel");
    }
}

package com.tanservices.shipment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShipmentStateMachineTest {

    @Autowired
    private StateMachine<ShipmentStatus, String> stateMachine;

    @Test
    public void testShipmentStateMachine() {
        stateMachine.start();
        assertEquals(ShipmentStatus.NEW, stateMachine.getState().getId());

        stateMachine.sendEvent("complete");
        assertEquals(ShipmentStatus.COMPLETED, stateMachine.getState().getId());

        stateMachine.sendEvent("lose");
        assertEquals(ShipmentStatus.COMPLETED, stateMachine.getState().getId());

        stateMachine.sendEvent("cancel");
        assertEquals(ShipmentStatus.COMPLETED, stateMachine.getState().getId());
    }
}
package com.tanservices.shipment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.StateMachine;

@Service
@Slf4j
public class ShipmentStateMachineService {

    private final StateMachine<ShipmentStatus, ShipmentStateMachine.ShipmentEvent> stateMachine;

    public ShipmentStateMachineService() throws Exception {
        this.stateMachine = ShipmentStateMachine.buildMachine();
        this.stateMachine.start();
    }

    public StateMachine<ShipmentStatus, ShipmentStateMachine.ShipmentEvent> getStateMachine() {
        return stateMachine;
    }

    public void processShipmentState(Shipment shipment, ShipmentStateMachine.ShipmentEvent shipmentEvent) {
        // Reset the state machine to the current state of the shipment
        resetStateMachine(shipment.getStatus());

        stateMachine.sendEvent(shipmentEvent);
        ShipmentStatus currentState = stateMachine.getState().getId();

        // Update shipment status based on current state of state machine
        shipment.setStatus(currentState);

        log.info("Current state for shipment id " + shipment.getId() + " is " + currentState);
    }

    private void resetStateMachine(ShipmentStatus shipmentStatus) {
        this.stateMachine.stop();
        this.stateMachine.getStateMachineAccessor().doWithAllRegions(accessor -> accessor.resetStateMachine(new DefaultStateMachineContext<>(shipmentStatus, null, null, null)));
        this.stateMachine.start();
    }
}

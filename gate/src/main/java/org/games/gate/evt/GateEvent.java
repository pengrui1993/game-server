package org.games.gate.evt;

public interface GateEvent {
    default GateEventType type(){
        return GateEventType.NULL;
    }
}

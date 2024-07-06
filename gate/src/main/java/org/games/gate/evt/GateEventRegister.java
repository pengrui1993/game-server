package org.games.gate.evt;

public interface GateEventRegister {
    void on(GateEventType type,GateEventListener listener);
    void off(GateEventType type,GateEventListener listener);
}

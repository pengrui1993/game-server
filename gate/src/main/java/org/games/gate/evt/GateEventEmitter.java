package org.games.gate.evt;

public interface GateEventEmitter {
    default void emit(GateEvent evt){}
}

package org.games.gate.evt;


public class ConnectedEvent implements GateEvent{
    public final Object ctx;
    public ConnectedEvent(Object ctx) {
        this.ctx = ctx;
    }
    public GateEventType type(){
        return GateEventType.CONNECTED;
    }
}

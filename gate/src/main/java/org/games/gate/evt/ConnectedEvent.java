package org.games.gate.evt;


public class ConnectedEvent implements GateEvent{
    public final Object fd;
    public ConnectedEvent(Object fd) {
        this.fd = fd;
    }
    public GateEventType type(){
        return GateEventType.CONNECTED;
    }
}

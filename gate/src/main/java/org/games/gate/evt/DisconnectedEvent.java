package org.games.gate.evt;

public class DisconnectedEvent implements GateEvent{
    public final Object ctx;

    public DisconnectedEvent(Object ctx) {
        this.ctx = ctx;
    }

    @Override
    public GateEventType type() {
        return GateEventType.DISCONNECTED;
    }
}

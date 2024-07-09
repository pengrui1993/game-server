package org.games.gate.evt;

import org.games.gate.session.Session;

public class DisconnectedEvent implements GateEvent{
    public final Session ctx;

    public DisconnectedEvent(Session ctx) {
        this.ctx = ctx;
    }

    @Override
    public GateEventType type() {
        return GateEventType.DISCONNECTED;
    }
}

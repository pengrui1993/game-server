package org.games.gate.evt;

import org.games.gate.session.Session;

public class ConnectionErrorEvent implements GateEvent{

    public final Session session;
    public ConnectionErrorEvent(Session session) {
        this.session = session;
    }

    @Override
    public GateEventType type() {
        return GateEventType.CONNECTION_ERR;
    }
}

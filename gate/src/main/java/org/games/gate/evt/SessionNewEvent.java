package org.games.gate.evt;

import org.games.gate.Session;

public class SessionNewEvent implements GateEvent{

    public SessionNewEvent(Session session) {
    }

    @Override
    public GateEventType type() {
        return GateEventType.SESSION_NEW;
    }
}

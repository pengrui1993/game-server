package org.games.gate.evt;

import org.games.event.Event;
import org.games.gate.session.Session;

public class NodeEvent implements GateEvent {

    public final Event event;
    public final Session session;
    public NodeEvent(Event event, Session session) {
        this.event = event;
        this.session = session;
    }

    @Override
    public GateEventType type() {
        return GateEventType.NODE_EVENT;
    }
}

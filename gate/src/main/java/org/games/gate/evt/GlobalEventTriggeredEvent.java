package org.games.gate.evt;

import org.games.event.Event;

public class GlobalEventTriggeredEvent implements GateEvent{
    public final Event globalEvent;
    public final Object fd;
    public GlobalEventTriggeredEvent(Object fd, Event evt) {
        this.fd = fd;
        globalEvent = evt;
    }
    @Override
    public GateEventType type() {
        return GateEventType.GLOBAL_EVENT_TRIGGERED;
    }
}

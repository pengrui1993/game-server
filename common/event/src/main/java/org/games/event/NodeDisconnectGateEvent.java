package org.games.event;

import org.games.constant.EventType;
import org.games.constant.SystemRoleType;

public class NodeDisconnectGateEvent implements Event{
    public int nodeType = SystemRoleType.GATE.id;
    @Override
    public EventType type() {
        return EventType.NODE_DISCONNECT_GATE;
    }
}

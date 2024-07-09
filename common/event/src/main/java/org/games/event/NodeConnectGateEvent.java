package org.games.event;

import org.games.constant.EventType;
import org.games.constant.SystemRoleType;

public class NodeConnectGateEvent implements Event{
    public int nodeType = SystemRoleType.GATE.id;
    @Override
    public EventType type() {
        return EventType.NODE_CONNECT_GATE;
    }
}

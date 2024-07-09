package org.games.event;

import org.games.constant.EventType;
import org.games.constant.SystemRoleType;

public class NodeDisconnectEvent extends AbstractEvent{
    public int nodeType = SystemRoleType.GATE.id;
    public int client = SystemRoleType.BUS.id;
    public int server = SystemRoleType.GATE.id;
    @Override
    public EventType type() {
        return EventType.NODE_DISCONNECT;
    }
}

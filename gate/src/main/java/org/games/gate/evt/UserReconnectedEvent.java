package org.games.gate.evt;

public class UserReconnectedEvent implements GateEvent{

    @Override
    public GateEventType type() {
        return GateEventType.USER_RECONNECTED;
    }
}

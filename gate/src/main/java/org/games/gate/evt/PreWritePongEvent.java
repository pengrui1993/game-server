package org.games.gate.evt;

import org.games.cmd.Command;
import org.games.gate.Session;
import org.games.message.PongMessage;

public class PreWritePongEvent implements GateEvent {

    public PreWritePongEvent(Session session, Command command, PongMessage msg) {
    }

    @Override
    public GateEventType type() {
        return GateEventType.PRE_PONG;
    }
}

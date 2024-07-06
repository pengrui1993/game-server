package org.games.gate.evt;

import org.games.cmd.Command;
import org.games.gate.Session;
import org.games.message.PongMessage;

public class PostWritePongEvent implements GateEvent {

    public PostWritePongEvent(PreWritePongEvent pre) {
    }

    @Override
    public GateEventType type() {
        return GateEventType.POST_PONG;
    }
}

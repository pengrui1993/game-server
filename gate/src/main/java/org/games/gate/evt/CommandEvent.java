package org.games.gate.evt;

import org.games.cmd.Command;
import org.games.gate.session.Session;

public class CommandEvent implements GateEvent {
    public final Session session;
    public final Command cmd;
    public CommandEvent(Session session, Command cmd) {
        this.session = session;
        this.cmd = cmd;
    }
    @Override
    public GateEventType type() {
        return GateEventType.COMMAND_EVENT;
    }
}

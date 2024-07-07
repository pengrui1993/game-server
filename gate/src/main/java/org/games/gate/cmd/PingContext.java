package org.games.gate.cmd;

import org.games.cmd.Command;
import org.games.cmd.CommandContext;
import org.games.gate.session.Session;
import org.games.gate.evt.GateEventEmitter;

public class PingContext implements CommandContext {
    public final PingCommand cmd;
    private GateEventEmitter emitter;
    private Session session;
    public PingContext(PingCommand command, Session session, GateEventEmitter emitter) {
        this.cmd = command;
        this.session = session;
        this.emitter = emitter;
    }
    @Override
    public Command getCommand() {
        return cmd;
    }
    public GateEventEmitter getEmitter() {
        return emitter;
    }
    public Session getSession() {
        return session;
    }
}

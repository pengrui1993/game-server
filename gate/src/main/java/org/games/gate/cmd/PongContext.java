package org.games.gate.cmd;

import org.games.cmd.Command;
import org.games.cmd.CommandContext;
import org.games.gate.Session;
import org.games.gate.evt.GateEventEmitter;

public class PongContext implements CommandContext {
    private Command cmd;



    private GateEventEmitter emitter;
    private Session session;
    public PongContext(Command command, Session session, GateEventEmitter emitter) {
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

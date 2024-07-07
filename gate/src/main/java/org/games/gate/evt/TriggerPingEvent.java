package org.games.gate.evt;

import org.games.cmd.Command;
import org.games.gate.cmd.PingCommand;
import org.games.gate.session.Session;

public class TriggerPingEvent implements GateEvent{
    public final Command command;
    public final Session session;
    public final GateEventEmitter emitter;
    public TriggerPingEvent(Session session, PingCommand command, GateEventEmitter emitter) {
        this.session=session;
        this.command=command;
        this.emitter=emitter;
    }
    @Override
    public GateEventType type() {
        return GateEventType.TRIGGER_PING;
    }
}

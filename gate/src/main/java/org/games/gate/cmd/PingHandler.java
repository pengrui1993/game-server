package org.games.gate.cmd;

import org.games.cmd.Command;
import org.games.cmd.CommandContext;
import org.games.cmd.CommandHandler;
import org.games.support.server.ProgramContext;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.PreWritePongEvent;
import org.games.gate.evt.TriggerPingEvent;
import org.games.gate.session.Session;


public class PingHandler implements CommandHandler {
    private final ProgramContext pc;
    public PingHandler(ProgramContext pc) {
        this.pc = pc;
    }
    @Override
    public void handle(Command cmd,CommandContext cc) {
        CommandContextImpl ctx = (CommandContextImpl)cc;
        final Session session = ctx.session;
        final PingCommand command = (PingCommand)cmd;
        final GateEventEmitter emitter = pc.get(GateEventEmitter.class);
        final TriggerPingEvent tpe;
        emitter.emit(tpe=new TriggerPingEvent(session, command,emitter));
        final PreWritePongEvent pre = new PreWritePongEvent(tpe);
        emitter.emit(pre);
    }
}

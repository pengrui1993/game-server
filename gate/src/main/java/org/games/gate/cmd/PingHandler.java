package org.games.gate.cmd;

import org.games.cmd.Command;
import org.games.cmd.CommandContext;
import org.games.cmd.CommandHandler;
import org.games.gate.evt.TriggerPingEvent;
import org.games.gate.msg.PongMessage;
import org.games.gate.session.Session;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.PostWritePongEvent;
import org.games.gate.evt.PreWritePongEvent;
import org.springframework.stereotype.Component;


@Component
public class PingHandler implements CommandHandler {
    @Override
    public void handle(CommandContext cc) {
        final PingContext ctx = (PingContext)cc;
        final Session session = ctx.getSession();
        final PingCommand command = ctx.cmd;
        final GateEventEmitter emitter = ctx.getEmitter();
        final TriggerPingEvent tpe;
        emitter.emit(tpe=new TriggerPingEvent(session, command,emitter));
        final PreWritePongEvent pre = new PreWritePongEvent(tpe);
        emitter.emit(pre);
    }
}

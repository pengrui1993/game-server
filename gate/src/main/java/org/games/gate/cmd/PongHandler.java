package org.games.gate.cmd;

import org.games.cmd.Command;
import org.games.cmd.CommandContext;
import org.games.cmd.CommandHandler;
import org.games.gate.Session;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.PostWritePongEvent;
import org.games.gate.evt.PreWritePongEvent;
import org.games.message.PongMessage;
import org.springframework.stereotype.Component;


@Component
public class PongHandler implements CommandHandler {
    @Override
    public void handle(CommandContext cc) {
        final PongContext ctx = (PongContext)cc;
        final Session session = ctx.getSession();
        final Command command = ctx.getCommand();
        final PongMessage msg = new PongMessage();
        final PreWritePongEvent pre = new PreWritePongEvent(session, command, msg);
        final GateEventEmitter emitter = ctx.getEmitter();
        emitter.emit(pre);
        final Runnable r = ()-> emitter.emit(new PostWritePongEvent(pre));
        session.writeAndFlush(msg,r);
    }
}

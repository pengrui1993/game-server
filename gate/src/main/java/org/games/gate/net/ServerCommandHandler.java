package org.games.gate.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.games.cmd.Command;
import org.games.gate.ProgramContext;
import org.games.gate.evt.CommandEvent;
import org.games.gate.evt.ConnectionErrorEvent;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.session.Session;
import org.games.gate.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerCommandHandler extends SimpleChannelInboundHandler<Command> {
    static final Logger log = LoggerFactory.getLogger(ServerCommandHandler.class);
    final ProgramContext pc;
    public ServerCommandHandler(ProgramContext pc) {
        this.pc = pc;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        pc.exec(()->{
            Session session = pc.get(SessionManager.class).get(ctx.channel());
            CommandEvent evt = new CommandEvent(session,command);
            pc.get(GateEventEmitter.class).emit(evt);
        });
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
        try{
            super.exceptionCaught(ctx, cause);
        }catch (Throwable t){
            log.error(t.getMessage(),t);
        }
        pc.exec(()-> pc.get(GateEventEmitter.class)
                .emit(new ConnectionErrorEvent(pc.get(SessionManager.class).get(ctx.channel()))));
    }
}

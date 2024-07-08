package org.games.gate.net;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.games.cmd.Command;
import org.games.gate.cmd.ContextFactory;
import org.games.gate.cmd.HandlerFinder;
import org.games.gate.evt.ConnectedEvent;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.ConnectionErrorEvent;
import org.games.gate.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Handler implementation for the echo server.
 */
@Sharable
@Component
public class ServerHandler extends SimpleChannelInboundHandler<Command> {
    static final Logger log = LoggerFactory.getLogger(ServerHandler.class);
    @Resource
    private GateEventEmitter emitter;
    public interface Accessor{
        Session get(Object ctx);
    }
    @Resource
    private Accessor sessions;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        Session session;
        if(Objects.isNull(session= sessions.get(ctx))){
            emitter.emit(new ConnectedEvent(ctx));
            session= sessions.get(ctx);
            if(Objects.isNull(session)){
                log.error("connected event must be sync to create a session");
                System.exit(-1);
            }
        }
        session.onCommand(command);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
        try{
            super.exceptionCaught(ctx, cause);
        }catch (Throwable t){
            log.error(t.getMessage(),t);
        }
        emitter.emit(new ConnectionErrorEvent(sessions.get(ctx)));
    }
}

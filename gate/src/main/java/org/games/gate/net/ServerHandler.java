package org.games.gate.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.games.cmd.Command;
import org.games.event.Sync;
import org.games.gate.App;
import org.games.gate.cmd.ContextFactory;
import org.games.gate.cmd.HandlerFinder;
import org.games.gate.evt.ConnectedEvent;
import org.games.gate.evt.DisconnectedEvent;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.ConnectionErrorEvent;
import org.games.gate.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        if(Objects.isNull(sessions.get(channel))){
            App.exec(()->{
                Session session;
                if(Objects.isNull(sessions.get(channel))){
                    emitter.emit(new ConnectedEvent(channel));
                    session= sessions.get(channel);
                    if(Objects.isNull(session)){
                        log.error("connected event must be sync to create a session");
                        System.exit(-1);
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        App.exec(()->{
            Session session;
            if(Objects.nonNull(session=sessions.get(channel))){
                emitter.emit(new DisconnectedEvent(session));
                session= sessions.get(channel);
                if(Objects.nonNull(session)){
                    log.error("disconnected event must be sync to remove a session");
                    System.exit(-1);
                }
            }else{
                log.warn("require a session but not");
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        App.exec(()-> sessions.get(ctx.channel()).onCommand(command));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
        try{
            super.exceptionCaught(ctx, cause);
        }catch (Throwable t){
            log.error(t.getMessage(),t);
        }
        App.exec(()-> emitter.emit(new ConnectionErrorEvent(sessions.get(ctx.channel()))));
    }
}

package org.games.gate.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.games.event.Event;
import org.games.gate.ProgramContext;
import org.games.gate.evt.ConnectedEvent;
import org.games.gate.evt.DisconnectedEvent;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.NodeEvent;
import org.games.gate.session.Session;
import org.games.gate.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class ServerEventHandler extends ChannelInboundHandlerAdapter {
    static final Logger log = LoggerFactory.getLogger(ServerCommandHandler.class);
    final ProgramContext pc;
    public ServerEventHandler(ProgramContext pc) {
        this.pc = pc;
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        pc.exec(()->{
            SessionManager sessions = pc.get(SessionManager.class);
            Session session;
            if(Objects.nonNull(session=sessions.get(channel))){
                pc.get(GateEventEmitter.class)
                        .emit(new DisconnectedEvent(session));
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        SessionManager sessions = pc.get(SessionManager.class);
        if(Objects.isNull(sessions.get(channel))){
            pc.exec(()->{
                Session session;
                if(Objects.isNull(sessions.get(channel))){
                    pc.get(GateEventEmitter.class)
                            .emit(new ConnectedEvent(channel));
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Event event){
            pc.exec(()->{
                final Session session = pc.get(SessionManager.class).get(ctx.channel());
                pc.get(GateEventEmitter.class).emit(new NodeEvent(event,session));
            });
        }else{
            ctx.fireChannelRead(msg);
        }
    }
}

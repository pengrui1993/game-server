package org.games.gate.session;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.games.cmd.Command;
import org.games.gate.cmd.ContextFactory;
import org.games.gate.cmd.HandlerFinder;
import org.games.gate.evt.GateEventEmitter;
import org.games.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

class PlayerSession implements Session{
    static final Logger log = LoggerFactory.getLogger(PlayerSession.class);
    private final ApiForSession manager;
    private GateEventEmitter emitter;
    PlayerSession(Object fd, ApiForSession sessionManager){
        this.fd = (ChannelHandlerContext)fd;
        this.manager = sessionManager;
        final ContextFactory ccf = sessionManager.getCommandContextFactory();
        final HandlerFinder chf = sessionManager.getCommandHandlerFinder();
        target = command -> chf.find(command.type()).handle(ccf.factory(command,this,emitter));
        manager.register(fd,this);
    }
    Handler target;
    ChannelHandlerContext fd;
    static final int NO_ROOM = 0;
    int roomId = NO_ROOM;
    static final String NO_LOGIN = null;
    String userId = NO_LOGIN;
    int globalMsgId;
    int userMsgId;
    long lastMsgTime = 0L;
    static int nextGlobalMsgId = 0;
    boolean isLoginDone(){ return !Objects.equals(userId, NO_LOGIN);}
    boolean isPlayingInGame(){ return roomId!=NO_ROOM;}
    void login(String userId) {
        if(!Objects.equals(NO_LOGIN, this.userId)){
            log.error("already login");
            System.exit(-1);
        }
        this.userId = userId;
    }
    @Override
    public void writeAndFlush(Message msg, Runnable r) {
        fd.writeAndFlush(msg).addListener((ChannelFutureListener) cf -> r.run());
    }
    @Override
    public void onCommand(Command command) {
        target.handle(command);
    }
}

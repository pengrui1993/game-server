package org.games.gate.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.games.gate.ProgramContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

class PlayerSession implements Session{
    static final Logger log = LoggerFactory.getLogger(PlayerSession.class);
    ProgramContext pc;
    public PlayerSession(Object ctx, ProgramContext pc) {
        this.fd = Channel.class.cast(ctx);
        this.pc = pc;
        this.pc.get(SessionManager.class).register(fd,this);
    }
    Channel fd;
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
    @Override
    public SessionType type() {
        return SessionType.USER;
    }
    void login(String userId) {
        if(!Objects.equals(NO_LOGIN, this.userId)){
            log.error("already login");
            System.exit(-1);
        }
        this.userId = userId;
    }
    @Override
    public void writeAndFlush(Object data, Runnable r) {
        if(Objects.isNull(data))return;
        if(Objects.isNull(r))fd.writeAndFlush(data);
        else fd.writeAndFlush(data)
                .addListener((ChannelFutureListener) cf -> r.run());
    }

    @Override
    public Object getFd() {
        return fd;
    }
}

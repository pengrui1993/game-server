package org.games.gate;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.games.gate.evt.*;
import org.games.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class SessionManager implements ServerHandler.Accessor{
    static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final Map<Object,SessionImpl> allSessions = new HashMap<>();
    private final Map<String,SessionImpl> userToSession = new HashMap<>();
    class SessionImpl implements Session{


        enum State{
            CONNED,LOGIN_DONE,DISCONNECTED
        }
        SessionImpl(Object fd){
            allSessions.put(fd,this);
            this.fd = (ChannelHandlerContext)fd;
        }
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
    }
    private final GateEventListener onConnected=(GateEvent ev)->{
        final ConnectedEvent e = ConnectedEvent.class.cast(ev);
        final GateEventEmitter emitter = this.emitter;
        emitter.emit(new SessionNewEvent(new SessionImpl(e.ctx)));
    };
    private final GateEventListener onConnectionError = (GateEvent ev)->{
        final ConnectionErrorEvent e = ConnectionErrorEvent.class.cast(ev);
        final SessionImpl session = (SessionImpl)e.session;
        if(Objects.isNull(allSessions.get(session.fd))){
            log.error("conn err but no created session");
            System.exit(-1);
        }
        allSessions.remove(session.fd);
    };

    private final GateEventListener onLoginDone=(GateEvent ev)->{
        final UserLoginDoneEvent e = UserLoginDoneEvent.class.cast(ev);
        SessionImpl session = get0(e.ctx);
        session.login(Objects.requireNonNull(e.userId));

    };
    private final GateEventListener onDisconnected = (GateEvent ev)->{
        DisconnectedEvent e = DisconnectedEvent.class.cast(ev);
        SessionImpl session = get0(e.ctx);
        //TODO
    };
    private final GateEventListener onDiscardReconnect = (GateEvent ev)->{};

    private final GateEventListener onUserReEnterRoom=(GateEvent ev)->{
        final UserReconnectedEvent e = UserReconnectedEvent.class.cast(ev);
        String userId = "";
        final SessionImpl session = userToSession.get(userId);
        //TODO
    };
    public SessionManager(){}
    private void init(){
        register.on(GateEventType.CONNECTED,this.onConnected);
        register.on(GateEventType.CONNECTION_ERR,this.onConnectionError);
        register.on(GateEventType.USER_LOGIN_DONE,this.onLoginDone);
        register.on(GateEventType.DISCONNECTED,this.onDisconnected);
    }
    private void destroy(){

    }
    @Resource
    private GateEventRegister register;
    @Resource
    private GateEventEmitter emitter;
    @Override
    public Session get(Object ctx) {
        return get0(ctx);
    }
    private SessionImpl get0(Object ctx){
        return allSessions.get(ctx);
    }
    //tick per second to clear useless data?
    void onUpdate(){}
}

package org.games.gate.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.games.support.server.ProgramContext;
import org.games.gate.evt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class SessionManager{
    static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    class AllSessions{
        private final Map<Object,Session> allSessions = new HashMap<>();
        public void register(Object fd, Session session) {
            allSessions.put(fd,session);
        }
        public Session unregister(Object fd){
            return allSessions.remove(fd);
        }

        public void foreach(SessionConsumer c) {
            allSessions.values().forEach(c);
        }

        public Session get(Object ctx) {
            return allSessions.get(ctx);
        }
    }
    interface SessionConsumer extends Consumer<Session>{}
    class UsersSessions{
        private final Map<String,Session> userToSession = new HashMap<>();

        public Session get(String userId) {
            return userToSession.get(userId);
        }
    }
    private final AllSessions allSessions = new AllSessions();
    private final UsersSessions userToSession = new UsersSessions();
    @Resource
    private ProgramContext pc;

    enum State{
        CONNED,LOGIN_DONE,DISCONNECTED
    }
    private final GateEventListener onConnected=(GateEvent ev)->{
        final ConnectedEvent e = ConnectedEvent.class.cast(ev);
        final GateEventEmitter emitter = this.emitter;
        Session session=new NoRoleSession(e.fd,pc);
        allSessions.register(e.fd,session);
        emitter.emit(new SessionNewEvent(session));
        session.writeAndFlush("hello netty".getBytes(StandardCharsets.UTF_8));
    };
    private final GateEventListener onDisconnected = (GateEvent ev)->{
        DisconnectedEvent e = DisconnectedEvent.class.cast(ev);
        allSessions.unregister(e.ctx.getFd());
    };
    private final GateEventListener onConnectionError = (GateEvent ev)->{
        final ConnectionErrorEvent e = ConnectionErrorEvent.class.cast(ev);
        if(Objects.isNull(e.session)){
            log.warn("conn err but no created session");
        }else{
            Session session = e.session;
            allSessions.unregister(session.getFd());
        }

    };

    private final GateEventListener onLoginDone=(GateEvent ev)->{
        final UserLoginDoneEvent e = UserLoginDoneEvent.class.cast(ev);
        Session session = get(e.ctx);

        //session.login(Objects.requireNonNull(e.userId));
    };

    private final GateEventListener onDiscardReconnect = (GateEvent ev)->{};

    private final GateEventListener onUserReEnterRoom=(GateEvent ev)->{
        final UserReconnectedEvent e = UserReconnectedEvent.class.cast(ev);
        String userId = "";
        final Session session = userToSession.get(userId);
        //TODO
    };
    public void register(Object fd,Session session){
        allSessions.register(fd,session);

    }
    @PostConstruct
    private void init(){
        register.on(GateEventType.CONNECTED,this.onConnected);
        register.on(GateEventType.CONNECTION_ERR,this.onConnectionError);
        register.on(GateEventType.USER_LOGIN_DONE,this.onLoginDone);
        register.on(GateEventType.DISCONNECTED,this.onDisconnected);
    }
    @PreDestroy
    private void destroy(){
        register.off(GateEventType.CONNECTED,this.onConnected);
        register.off(GateEventType.CONNECTION_ERR,this.onConnectionError);
        register.off(GateEventType.USER_LOGIN_DONE,this.onLoginDone);
        register.off(GateEventType.DISCONNECTED,this.onDisconnected);
    }
    @Resource
    private GateEventRegister register;
    @Resource
    private GateEventEmitter emitter;
    public Session get(Object ctx) {
        return allSessions.get(ctx);
    }
    //tick per second to clear useless data?
    void onUpdate(){}
    public void broadcast(String line) {
        final byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
        final ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        allSessions.foreach(s->s.writeAndFlush(buf));
    }
}

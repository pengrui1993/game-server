package org.games.gate.session;

import io.netty.buffer.Unpooled;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.games.gate.App;
import org.games.gate.cmd.ContextFactory;
import org.games.gate.cmd.HandlerFinder;
import org.games.gate.core.AuthNode;
import org.games.gate.net.Server;
import org.games.gate.net.ServerHandler;
import org.games.gate.evt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class SessionManager
        implements ServerHandler.Accessor
                    ,Session.ApiForSession
                    , AuthNode.SessionAccessor
                    , Server.SessionAccessor
{
    static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final Map<Object,Session> allSessions = new HashMap<>();
    private final Map<String,Session> userToSession = new HashMap<>();
    @Resource
    private ContextFactory ccf;
    @Resource
    private HandlerFinder chf;
    @Override
    public ContextFactory getCommandContextFactory() {
        return ccf;
    }
    @Override
    public HandlerFinder getCommandHandlerFinder() {
        return chf;
    }
    @Override
    public void register(Object fd, Session session) {
        allSessions.put(fd,session);
    }
    enum State{
        CONNED,LOGIN_DONE,DISCONNECTED
    }
    private final GateEventListener onConnected=(GateEvent ev)->{
        final ConnectedEvent e = ConnectedEvent.class.cast(ev);
        final GateEventEmitter emitter = this.emitter;
        Session session;
        emitter.emit(new SessionNewEvent(session=new NoRoleSession(e.ctx,this)));
        App.exec(()-> {
            session.writeAndFlush("hello netty".getBytes(StandardCharsets.UTF_8));
        });
    };
    private final GateEventListener onDisconnected = (GateEvent ev)->{
        DisconnectedEvent e = DisconnectedEvent.class.cast(ev);
        allSessions.remove(e.ctx.getFd());
    };
    private final GateEventListener onConnectionError = (GateEvent ev)->{
        final ConnectionErrorEvent e = ConnectionErrorEvent.class.cast(ev);
        if(Objects.isNull(e.session)){
            log.warn("conn err but no created session");
        }else{
            Session session = e.session;
            allSessions.remove(session.getFd());
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
    public SessionManager(){}
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
    @Override
    public Session get(Object ctx) {
        return allSessions.get(ctx);
    }
    //tick per second to clear useless data?
    void onUpdate(){}
    public void broadcast(String line) {
        allSessions.values().forEach(s->s.writeAndFlush(Unpooled.wrappedBuffer(line.getBytes(StandardCharsets.UTF_8))));
    }
}

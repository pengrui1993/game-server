package org.games.gate.core;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.games.constant.Const;
import org.games.event.Event;
import org.games.event.NodeConnectEvent;
import org.games.event.NodeDisconnectEvent;
import org.games.gate.evt.GateEventListener;
import org.games.gate.evt.GateEventRegister;
import org.games.gate.evt.GateEventType;
import org.games.gate.evt.GlobalEventTriggeredEvent;
import org.games.gate.session.Session;
import org.games.gate.session.SessionManager;
import org.games.support.server.ProgramContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

//@Component
public class AuthNode {
    static final Logger log = LoggerFactory.getLogger(AuthNode.class);
//    @Override
    public Session getSession() {
        return authNodeSession;
    }
    @Resource
    private ProgramContext pc;
    @PostConstruct
    private void init(){
        pc.postGet(GateEventRegister.class,register->{
            register.on(GateEventType.GLOBAL_EVENT_TRIGGERED,onGlobalEventTriggered);
        });
        //GateEventType.GLOBAL_EVENT_TRIGGERED
    }
    @PreDestroy
    private void destroy(){
        pc.get(GateEventRegister.class).off(GateEventType.GLOBAL_EVENT_TRIGGERED,onGlobalEventTriggered);
    }
    Session authNodeSession;
    void onUpdate(){
        Session s = authNodeSession;
    }
    public boolean isAuthSession(Session session){
        if(Objects.isNull(session))return false;
        return this.authNodeSession == session;
    }
    private final GateEventListener onGlobalEventTriggered = (e)->{
        GlobalEventTriggeredEvent ev = GlobalEventTriggeredEvent.class.cast(e);
        Event evt = ev.globalEvent;
        switch (evt.type()){
            case NODE_CONNECT ->{
                NodeConnectEvent ge = NodeConnectEvent.class.cast(evt);
                if (ge.nodeType == Const.AUTH_TYPE_ID) {
                    System.out.println("connected");
                    Session session = pc.get(SessionManager.class).get(ev.fd);
                    authNodeSession = session;
                    session.writeAndFlush("connected".getBytes(StandardCharsets.UTF_8)
                            , () -> System.out.println("ack done"));
                }
            }
            case NODE_DISCONNECT -> {
                NodeDisconnectEvent ge = NodeDisconnectEvent.class.cast(evt);
                if (ge.nodeType == Const.AUTH_TYPE_ID) {
                    System.out.println("disconnected");
                    Session session = authNodeSession;
                    authNodeSession = null;
                    pc.post(()->{
                        if(Objects.nonNull(pc.get(SessionManager.class).get(ev.fd))){
                            log.error("auth disconnected , the session should be deleted,but not");
                            System.exit(-1);
                        }
                    });
                    session.writeAndFlush("disconnected".getBytes(StandardCharsets.UTF_8)
                            , () -> System.out.println("ack done"));
                }
            }
            default->{

            }
        }
    };
}

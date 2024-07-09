package org.games.gate.core;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.games.gate.core.iface.Auth;
import org.springframework.stereotype.Component;

import org.games.constant.Const;
import org.games.event.Event;
import org.games.event.NodeConnectGateEvent;
import org.games.event.NodeDisconnectGateEvent;
import org.games.gate.App;
import org.games.gate.core.iface.Node;
import org.games.gate.evt.GateEventListener;
import org.games.gate.evt.GateEventRegister;
import org.games.gate.evt.GateEventType;
import org.games.gate.evt.GlobalEventTriggeredEvent;
import org.games.gate.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class AuthNode implements Auth {
    static final Logger log = LoggerFactory.getLogger(AuthNode.class);

    public interface SessionAccessor{
        Session get(Object ctx);
    }
    @Resource
    private SessionAccessor accessor;
    @PostConstruct
    private void init(){
        register.on(GateEventType.GLOBAL_EVENT_TRIGGERED,onGlobalEventTriggered);
    }
    @Resource
    private GateEventRegister register;//GateEventType.GLOBAL_EVENT_TRIGGERED
    Session authNodeSession;
    void onUpdate(){
        Session s = authNodeSession;
    }
    @Override
    public boolean isNodeSession(Session session) {
        return isAuthSession(session);
    }
    @Override
    public boolean isAuthSession(Session session){
        if(Objects.isNull(session))return false;
        return this.authNodeSession == session;
    }
    private final GateEventListener onGlobalEventTriggered = (e)->{
        GlobalEventTriggeredEvent ev = GlobalEventTriggeredEvent.class.cast(e);
        Event evt = ev.globalEvent;
        switch (evt.type()){
            case NODE_CONNECT_GATE ->{
                NodeConnectGateEvent ge = NodeConnectGateEvent.class.cast(evt);
                if (ge.nodeType == Const.AUTH_TYPE_ID) {
                    System.out.println("connected");
                    Session session = accessor.get(ev.fd);
                    authNodeSession = session;
                    session.writeAndFlush("connected".getBytes(StandardCharsets.UTF_8)
                            , () -> System.out.println("ack done"));
                }
            }
            case NODE_DISCONNECT_GATE -> {
                NodeDisconnectGateEvent ge = NodeDisconnectGateEvent.class.cast(evt);
                if (ge.nodeType == Const.AUTH_TYPE_ID) {
                    System.out.println("disconnected");
                    Session session = authNodeSession;
                    authNodeSession = null;
                    App.post(()->{
                        if(Objects.nonNull(accessor.get(ev.fd))){
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

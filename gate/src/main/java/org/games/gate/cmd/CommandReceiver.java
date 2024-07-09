package org.games.gate.cmd;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.games.cmd.Command;
import org.games.constant.CommandType;
import org.games.support.server.ProgramContext;
import org.games.gate.evt.CommandEvent;
import org.games.gate.evt.GateEventListener;
import org.games.gate.evt.GateEventRegister;
import org.games.gate.evt.GateEventType;
import org.games.gate.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class CommandReceiver {
    static final Logger log = LoggerFactory.getLogger(CommandReceiver.class);
    @Resource
    private ProgramContext pc;
    private final Map<Session, CommandContextImpl> map = new HashMap<>();
    @PostConstruct
    private void init(){
        pc.postGet(GateEventRegister.class,(emitter)-> emitter.on(GateEventType.COMMAND_EVENT,onCommandEvent));
    }
    @PreDestroy
    private void destroy(){
        pc.get(GateEventRegister.class).off(GateEventType.COMMAND_EVENT,onCommandEvent);
    }
    private final GateEventListener onCommandEvent = (ev)-> onCommandEvent(CommandEvent.class.cast(ev));
    private void onCommandEvent(CommandEvent ev){
        Session session = ev.session;
        Command cmd = ev.cmd;
        CommandContextImpl ctx = map.get(session);
        if(cmd.type()==CommandType.REQUEST_LOGIN){
            if(Objects.isNull(ctx)) {
                ctx = new CommandContextImpl(session);
                ctx.handler = new PingHandler(pc);
                map.put(session,ctx);
            }else{
                log.warn("already login");
            }
        }
        ctx.handler.handle(cmd,ctx);
    }
}

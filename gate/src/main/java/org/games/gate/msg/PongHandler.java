package org.games.gate.msg;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.games.gate.evt.*;
import org.games.gate.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PongHandler {
    static final Logger log = LoggerFactory.getLogger(PongHandler.class);
    @Resource
    private GateEventRegister register;
    @PostConstruct
    private void init(){
        register.on(GateEventType.POST_PONG, onPostPone);
    }
    @PreDestroy
    private void destroy(){
        register.off(GateEventType.POST_PONG, onPostPone);
    }
    private final GateEventListener onPostPone = ev->{
        final PostWritePongEvent evt = PostWritePongEvent.class.cast(ev);
        final TriggerPingEvent tpe = evt.pre.tpe;
        final GateEventEmitter emitter = tpe.emitter;
        final Session session = tpe.session;
        final PongMessage msg = new PongMessage();
        final Runnable r = ()-> emitter.emit(new PostWritePongEvent(evt.pre));
        session.writeAndFlush(msg,r);
    };

}

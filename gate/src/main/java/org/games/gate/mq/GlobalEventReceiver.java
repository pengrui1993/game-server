package org.games.gate.mq;

import jakarta.annotation.Resource;
import org.games.event.Event;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.GlobalEventTriggeredEvent;
import org.springframework.stereotype.Component;

@Component
public class GlobalEventReceiver{
    void onGlobalEventReceived(Event ge){
        GlobalEventTriggeredEvent evt = GlobalEventTriggeredEvent.class.cast(ge);
        //access event from network
    }
    @Resource
    private GateEventEmitter emitter;
}

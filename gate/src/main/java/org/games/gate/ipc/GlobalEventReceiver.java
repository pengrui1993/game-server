package org.games.gate.ipc;

import jakarta.annotation.Resource;
import org.games.event.Event;
import org.games.event.netty.EventDecodedTrigger;
import org.games.gate.App;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.GlobalEventTriggeredEvent;
import org.springframework.stereotype.Component;

@Component
public class GlobalEventReceiver implements EventDecodedTrigger {

    void onBusEventReceived(Event ge){
        //access event from network
    }
    @Resource
    private GateEventEmitter emitter;
    @Override
    public void triggerEvent(Object fd,Event evt) {
        App.exec(()->{
            emitter.emit(new GlobalEventTriggeredEvent(fd,evt));
        });
    }
}

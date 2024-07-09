package org.games.gate.evt;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GateEventDispatcher implements GateEventRegister,GateEventEmitter{
    static final Logger log = LoggerFactory.getLogger(GateEventDispatcher.class);
    private final Map<GateEventType, List<GateEventListener>> channels = new HashMap<>();
    public void on(GateEventType type,GateEventListener listener){
        List<GateEventListener> ls = channels.get(type);
        if(Objects.isNull(ls)){
            ls = new LinkedList<>();
            channels.put(type,ls);
        }
        ls.add(listener);
    }
    public void off(GateEventType type,GateEventListener listener){
        List<GateEventListener> ls = channels.get(type);
        ls.remove(listener);
        if(ls.isEmpty()){
            channels.remove(type);
        }
    }
    public void emit(GateEvent evt){
        List<GateEventListener> ls = channels.get(evt.type());
        if(Objects.isNull(ls)){
            log.warn("missing the event handler,event type:"+evt.type());
            return;
        }
        for (GateEventListener l : ls) {
            l.onEvent(evt);
        }
    }
    @Override
    public String toString() {
        return super.toString()+":\n"+channels;
    }
}

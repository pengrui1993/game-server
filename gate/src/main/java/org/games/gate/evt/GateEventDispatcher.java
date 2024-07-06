package org.games.gate.evt;


import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GateEventDispatcher implements GateEventRegister{
    private final Map<GateEventType, List<GateEventListener>> channels = new HashMap<>();
    public void on(GateEventType type,GateEventListener listener){
        List<GateEventListener> ls = channels.get(type);
        if(Objects.isNull(ls)){
            ls = new LinkedList<>();
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
        for (GateEventListener l : ls) {
            l.onEvent(evt);
        }
    }
}

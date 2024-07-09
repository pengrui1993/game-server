package org.games.gate.mq;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.games.gate.evt.GateEventListener;
import org.games.gate.evt.GateEventRegister;
import org.games.gate.evt.GateEventType;

public class GlobalEventSender {
    public interface BusHandler{
        default void onGateEventHappenedTellOthersComponents(){}
    }
    @Resource
    private GateEventRegister register;
    @Resource
    private BusHandler busHandler;
    @PostConstruct
    private void init(){
        register.on(GateEventType.NULL,onNulEvent);
    }
    @PreDestroy
    private void destroy(){
        register.off(GateEventType.NULL,onNulEvent);
    }
    private final GateEventListener onNulEvent = (e)->{
        busHandler.onGateEventHappenedTellOthersComponents();
    };
}

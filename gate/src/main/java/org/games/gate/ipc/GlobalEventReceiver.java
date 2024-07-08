package org.games.gate.ipc;

import org.games.event.Event;
import org.games.gate.evt.GateEventEmitter;

public class GlobalEventReceiver {

    private GateEventEmitter emitter;

    void onBusEventReceived(Event ge){
        //access event from network
    }
}

package org.games.gate.evt;

public class PreWritePongEvent implements GateEvent {
    public final TriggerPingEvent tpe;
    public PreWritePongEvent(TriggerPingEvent e) {
        this.tpe = e;
    }

    @Override
    public GateEventType type() {
        return GateEventType.PRE_PONG;
    }
}

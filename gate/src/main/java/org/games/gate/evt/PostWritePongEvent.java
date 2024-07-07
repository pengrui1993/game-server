package org.games.gate.evt;

public class PostWritePongEvent implements GateEvent {
    public final PreWritePongEvent pre;
    public PostWritePongEvent(PreWritePongEvent pre) {
        this.pre =pre;
    }
    @Override
    public GateEventType type() {
        return GateEventType.POST_PONG;
    }
}

package org.games.gate.evt;

public class UserLoginDoneEvent implements GateEvent{
    public final String userId;
    public final Object ctx;
    public UserLoginDoneEvent(Object ctx,String userId) {
        this.userId = userId;
        this.ctx = ctx;
    }
    @Override
    public GateEventType type() {
        return GateEventType.USER_LOGIN_DONE;
    }
}

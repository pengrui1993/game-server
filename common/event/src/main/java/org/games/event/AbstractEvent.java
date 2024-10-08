package org.games.event;

import org.games.constant.EventType;
import org.games.constant.SystemRoleType;

public abstract class AbstractEvent implements Event{
    public int sender = SystemRoleType.BUS.id;
    public int receiver = SystemRoleType.AUTH.id;
    public transient int value =100;
    public String msg = "hello";
    public boolean condition = false;
    public long senderTime = now();
    @Override
    public EventType type() {
        return EventType.HELLO;
    }
}

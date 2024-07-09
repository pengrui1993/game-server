package org.games.event.netty;

import org.games.event.Event;

public interface EventDecodedTrigger {
    void triggerEvent(Object fd,Event evt);
}

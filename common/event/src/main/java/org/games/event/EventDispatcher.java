package org.games.event;

import org.games.constant.EventType;

public interface EventDispatcher {
    void on(EventType type,EventListener listener);
    void off(EventType type,EventListener listener);
    void emit(Event evt);
}

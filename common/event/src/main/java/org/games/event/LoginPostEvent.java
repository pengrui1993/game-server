package org.games.event;

import org.games.constant.EventType;

public class LoginPostEvent extends AbstractEvent{
    @Override
    public EventType type() {
        return EventType.EVT_LOGIN_POST;
    }
}

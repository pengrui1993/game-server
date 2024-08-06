package org.games.logic.wolf.core;

public enum Event{
    NULL,CONNECTION,ACTION,CONFIG, SOUNDS,MESSAGE
    ;
    public static Event from(int type) {
        for (Event value : Event.values()) {
            if(value.ordinal()==type)
                return value;
        }
        return NULL;
    }
}
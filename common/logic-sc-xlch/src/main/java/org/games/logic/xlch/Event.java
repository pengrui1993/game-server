package org.games.logic.xlch;

public enum Event {
    NONE,ACTION,MESSAGE
    ;

    public static Event from(int type) {
        for (Event value : Event.values()) {
            if(value.ordinal()==type)
                return value;
        }
        return NONE;
    }
}

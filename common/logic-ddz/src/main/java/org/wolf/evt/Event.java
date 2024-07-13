package org.wolf.evt;

public enum Event{
    UNKNOWN,CONNECTION,ACTION,MESSAGE
    ;
    public static Event from(int type) {
        for (Event value : Event.values()) {
            if(value.ordinal()==type)
                return value;
        }
        return UNKNOWN;
    }
}
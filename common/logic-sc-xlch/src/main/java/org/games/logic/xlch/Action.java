package org.games.logic.xlch;

public enum Action {
    NONE,CHI,PENG,GANG,HU,GRAB,HAND,PASS
    ;

    public static Action from(int type) {
        for (Action value : Action.values()) {
            if(type==value.ordinal())return value;
        }
        return NONE;
    }
}

package org.wolf.action;

public enum Action {
    UNKNOWN
    ,JOIN
    ,LEFT
    ,START_GAME
    ,WOLF_KILL
    ,WITCH_ACTION
    ;

    public static Action from(int action) {
        for (Action value : Action.values()) {
            if(value.ordinal()==action)
                return value;
        }
        return UNKNOWN;
    }
}

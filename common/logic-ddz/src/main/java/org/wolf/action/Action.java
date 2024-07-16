package org.wolf.action;

public enum Action {
    UNKNOWN
    , TEST_DONE//for test
    ,JOIN
    ,LEFT
    ,START_GAME
    ,WOLF_KILL
    ,WITCH_ACTION
    ,PREDICTOR_ACTION
    ,RACE_CHOICE
    ,RACE_HANDS_DOWN
    ,RACE_STOP_TALKING
    ,RACE_VOTE
    ,ORDERING_DECISION
    ,TALKING_NEXT
    ,HUNTER_ACTION
    ,VOTING_VOTE
    ;

    public static Action from(int action) {
        for (Action value : Action.values()) {
            if(value.ordinal()==action)
                return value;
        }
        return UNKNOWN;
    }
}

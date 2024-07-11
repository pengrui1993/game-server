package org.games.logic.ddz;

public enum ComboType {
    UNKNOWN
    ,SINGLE
    ,DOUBLE
    ,SEQUENCE
    ,DOUBLE_SEQ
    ,THIRD
    ,THIRD_1
    ,THIRD_2
    ,DOUBLE_THIRD
    ,DOUBLE_THIRD_1_1
    ,DOUBLE_THIRD_2_2
    ,THI_THIRD_1_1_1
    ,THI_THIRD_2_2_2

    ,QUAD(5)
    ,QUAD_1
    ,QUAD_2
    ,DOUBLE_JOKER(9)
    ;
    public final int boomValue;
    ComboType(){
        boomValue = 0;
    }
    ComboType(int boom){
        boomValue = boom;
    }
}

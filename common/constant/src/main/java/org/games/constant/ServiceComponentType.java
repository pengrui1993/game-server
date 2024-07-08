package org.games.constant;

public enum ServiceComponentType {
    AUTH(Const.AUTH_TYPE_ID)
    ,BUS(Const.BUS_TYPE_ID)
    ,CONFIG(Const.CONFIGS_TYPE_ID)
    ,GATE(Const.GATE_TYPE_ID)
    ,LOGICS(Const.LOGICS_TYPE_ID)
    ,USERS(Const.USERS_TYPE_ID)
    ,MONITOR(Const.MONITOR_TYPE_ID)
    ;
    public final int id;
    ServiceComponentType(int id) {
        this.id = id;
    }
}

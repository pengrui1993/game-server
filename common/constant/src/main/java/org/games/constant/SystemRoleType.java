package org.games.constant;

public enum SystemRoleType {
    USER(Const.USER_TYPE_ID,false)
    ,AUTH(Const.AUTH_TYPE_ID)
    ,BUS(Const.BUS_TYPE_ID)
    ,CONFIG(Const.CONFIGS_TYPE_ID)
    ,GATE(Const.GATE_TYPE_ID)
    ,LOGICS(Const.LOGICS_TYPE_ID)
    ,USERS(Const.USERS_TYPE_ID)
    ,MONITOR(Const.MONITOR_TYPE_ID)
    ;
    public final int id;
    public final boolean isServerSide;
    SystemRoleType(int id) {
        this(id,true);
    }
    SystemRoleType(int id,boolean serverSide) {
        this.id = id;
        isServerSide = serverSide;
    }
}

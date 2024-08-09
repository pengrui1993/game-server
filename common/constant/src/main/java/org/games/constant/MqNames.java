package org.games.constant;

public enum MqNames {
    AUTH(SystemRoleType.AUTH,Const.MQ_AUTH_NAME)
    ,BUS(SystemRoleType.BUS,Const.MQ_BUS_NAME)
    ,CONFIG(SystemRoleType.CONFIG,Const.MQ_CONFIG_NAME)
    ,GATE(SystemRoleType.GATE,Const.MQ_GATE_NAME)
    ,LOGICS(SystemRoleType.LOGICS,Const.MQ_LOGICS_NAME)
    ,USERS(SystemRoleType.USERS,Const.MQ_USERS_NAME)
    ;
    public final SystemRoleType role;
    public final String name;
    public static final MqNames[] ALL ={AUTH,BUS,CONFIG,GATE,LOGICS,USERS};
    MqNames(SystemRoleType role, String name) {
        this.role = role;
        this.name = name;
    }
}

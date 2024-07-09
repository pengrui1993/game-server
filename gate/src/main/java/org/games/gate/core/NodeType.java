package org.games.gate.core;

import org.games.constant.SystemRoleType;

public enum NodeType {
    AUTH(SystemRoleType.AUTH.id)
    ,BUS(SystemRoleType.BUS.id)
    ,CONFIG(SystemRoleType.CONFIG.id)
    ,GATE(SystemRoleType.GATE.id)
    ,LOGICS(SystemRoleType.LOGICS.id)
    ,USERS(SystemRoleType.USERS.id)
    ,MONITOR(SystemRoleType.MONITOR.id)
    ;
    public final int id;
    NodeType(int id) {
        this.id = id;
    }
}

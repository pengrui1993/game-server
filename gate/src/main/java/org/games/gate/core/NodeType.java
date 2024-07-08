package org.games.gate.core;

import org.games.constant.Const;
import org.games.constant.ServiceComponentType;

public enum NodeType {
    AUTH(ServiceComponentType.AUTH.id)
    ,BUS(ServiceComponentType.BUS.id)
    ,CONFIG(ServiceComponentType.CONFIG.id)
    ,GATE(ServiceComponentType.GATE.id)
    ,LOGICS(ServiceComponentType.LOGICS.id)
    ,USERS(ServiceComponentType.USERS.id)
    ,MONITOR(ServiceComponentType.MONITOR.id)
    ;
    public final int id;
    NodeType(int id) {
        this.id = id;
    }
}

package org.games.gate.core.iface;

import org.games.gate.session.Session;

import java.util.Objects;

public interface Node {
    enum Type{
        NONE,GATE,AUTH,BUS,CONFIG,LOGICS,USERS
    }
    Type type();
    interface GateHandler{
        void register(Session session, Node node);
    }
    default boolean isNodeSession(Session session,Type type){
        if(Objects.isNull(getSession()))return false;
        return type==type();
    }
    Session getSession();
}

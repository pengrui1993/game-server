package org.games.gate.core.iface;

import org.games.gate.session.Session;

public interface Node {
    enum Type{
        NONE,AUTH,BUS,CONFIG,LOGICS,USERS
    }
    Type type();
    interface GateHandler{
        void register(Session session, Node node);
    }
    boolean isNodeSession(Session session);
}

package org.games.gate.core;

import org.games.gate.core.iface.Node;
import org.games.gate.session.Session;

class UnknownNode implements Node {
    Session session;
    GateHandler handler;
    UnknownNode(Session session, GateHandler handler){
        this.session = session;
        this.handler = handler;
        handler.register(session,this);
    }
    @Override
    public Type type() {
        return Type.NONE;
    }
}
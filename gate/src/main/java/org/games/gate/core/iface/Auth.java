package org.games.gate.core.iface;

import org.games.gate.session.Session;

public interface Auth extends Node{
    @Override
    default Type type(){return Type.AUTH;}
    boolean isAuthSession(Session session);
}

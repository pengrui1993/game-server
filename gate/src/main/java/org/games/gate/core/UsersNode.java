package org.games.gate.core;

import org.games.gate.core.iface.Node;
import org.games.gate.session.Session;

public class UsersNode implements Node {
    private Session usersNodeSession;
    @Override
    public Type type() {
        return Type.USERS;
    }
    @Override
    public Session getSession() {
        return usersNodeSession;
    }
}

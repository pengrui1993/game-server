package org.games.gate.core;

import org.games.gate.core.iface.Node;

public class UsersNode implements Node {
    @Override
    public Type type() {
        return Type.USERS;
    }
}

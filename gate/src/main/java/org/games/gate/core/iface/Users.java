package org.games.gate.core.iface;

public interface Users extends Node{
    @Override
    default Type type() {
        return Type.USERS;
    }
}

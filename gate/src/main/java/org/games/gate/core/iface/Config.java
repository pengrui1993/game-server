package org.games.gate.core.iface;

public interface Config extends Node{
    @Override
    default Type type() {
        return Type.CONFIG;
    }
}

package org.games.gate.core;

import org.games.gate.core.iface.Node;

public class ConfigNode implements Node {
    @Override
    public Type type() {
        return Type.CONFIG;
    }
}

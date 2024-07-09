package org.games.gate.core;

import org.games.gate.core.iface.Config;
import org.games.gate.core.iface.Node;
import org.games.gate.session.Session;

import java.util.Objects;

public class ConfigNode implements Config {

    Session configNodeSession;
    @Override
    public Session getSession() {
        return configNodeSession;
    }
}

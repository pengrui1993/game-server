package org.games.gate.core;

import org.games.gate.core.iface.Node;
import org.games.gate.ipc.GlobalEventSender;
import org.games.gate.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusNode implements Node
            , GlobalEventSender.BusHandler
{
    static final Logger log = LoggerFactory.getLogger(BusNode.class);
    @Override
    public Type type() {
        return Type.BUS;
    }
    @Override
    public Session getSession() {
        return busNodeSession;
    }
    Session busNodeSession;

}

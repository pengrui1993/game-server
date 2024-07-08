package org.games.gate.core;

import org.games.gate.core.iface.Node;
import org.games.gate.ipc.GlobalEventSender;

public class BusNode implements Node
            , GlobalEventSender.BusHandler
{
    @Override
    public Type type() {
        return Type.BUS;
    }
}

package org.games.event.netty;

import org.games.event.Event;
import org.games.event.NodeConnectGateEvent;

public class NodeConnectedEventDecoderHandler implements NodeEventDecoderHandler{
    @Override
    public Event decode(NodeEventHeader header, byte[] body) {
        NodeConnectGateEvent e = new NodeConnectGateEvent();
        e.nodeType = header.roleId;
        return e;
    }
}

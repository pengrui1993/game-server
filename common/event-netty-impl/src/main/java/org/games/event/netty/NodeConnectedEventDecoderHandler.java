package org.games.event.netty;

import org.games.event.Event;
import org.games.event.NodeConnectEvent;

public class NodeConnectedEventDecoderHandler implements NodeEventDecoderHandler{
    @Override
    public Event decode(NodeEventHeader header, byte[] body) {
        NodeConnectEvent e = new NodeConnectEvent();
        e.nodeType = header.roleId;
        return e;
    }
}

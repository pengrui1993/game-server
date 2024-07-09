package org.games.event.netty;

import org.games.event.Event;

public interface NodeEventDecoderHandler {
    Event decode(NodeEventHeader header, byte[] body);
}

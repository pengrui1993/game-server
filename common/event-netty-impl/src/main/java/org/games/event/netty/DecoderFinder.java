package org.games.event.netty;

public class DecoderFinder {
    public static NodeEventDecoderHandler find(int eventId) {
        NodeEventDecoderHandler handler = new NodeConnectedEventDecoderHandler();

        return handler;
    }
}

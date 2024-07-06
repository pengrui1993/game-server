package org.games.message;

import org.games.constant.MessageType;

public class PongMessage implements Message{
    public final long serverTime = now();
    @Override
    public MessageType type() {
        return MessageType.PONG;
    }
}

package org.games.gate.msg;

import org.games.constant.MessageType;
import org.games.message.Message;

public class PongMessage implements Message {
    public final long serverTime = now();
    @Override
    public MessageType type() {
        return MessageType.PONG;
    }
}

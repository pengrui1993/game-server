package org.games.gate.msg;

import org.games.constant.MessageType;
import org.games.message.Message;
import org.games.message.MessageEncoder;
import org.springframework.stereotype.Component;

@Component
public class PongMessageEncoder implements MessageEncoder {
    @Override
    public MessageType type() {
        return MessageType.PONG;
    }

    @Override
    public byte[] encode(Message msg) {
        return new byte[0];
    }
}

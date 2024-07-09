package org.games.gate.codec;

import org.games.constant.MessageType;
import org.games.gate.App;
import org.games.message.Message;
import org.games.message.MessageEncoder;

public interface EncoderHandler {
    static MessageEncoder getEncoder(MessageType type) {
        for (MessageEncoder encoder:
                App.ctx().getBean(App.class).gets(MessageEncoder.class)) {
            if(encoder.type()==type)
                return encoder;
        }
        return new MessageEncoder() {
            @Override
            public MessageType type() {
                return MessageType.NULL;
            }
            @Override
            public byte[] encode(Message msg) {
                return new byte[0];
            }
        };
    }
}

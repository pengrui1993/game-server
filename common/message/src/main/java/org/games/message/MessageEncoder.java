package org.games.message;

import org.games.constant.MessageType;

public interface MessageEncoder {
    MessageType type();
    byte[] encode(Message msg);
}

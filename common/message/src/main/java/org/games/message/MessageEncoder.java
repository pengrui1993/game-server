package org.games.message;

public interface MessageEncoder {
    byte[] encode(Message msg);
}

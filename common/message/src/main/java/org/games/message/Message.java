package org.games.message;

import org.games.constant.MessageType;

public interface Message {
    MessageType type();
    default long now(){ return System.currentTimeMillis();}
}

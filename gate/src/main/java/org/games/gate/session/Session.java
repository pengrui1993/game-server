package org.games.gate.session;

import org.games.message.Message;

public interface Session {
    void writeAndFlush(Message msg, Runnable r);
}

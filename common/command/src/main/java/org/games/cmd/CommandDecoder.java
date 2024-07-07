package org.games.cmd;

import org.games.constant.CommandType;

public interface CommandDecoder {
    CommandType type();
    Command encode(CommandHeader header,byte[] body);
}

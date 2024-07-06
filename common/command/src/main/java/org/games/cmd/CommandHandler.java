package org.games.cmd;

import org.games.constant.CommandType;

public interface CommandHandler {
    default CommandType type(){return CommandType.NULL;}
    void handle(CommandContext ctx);
}

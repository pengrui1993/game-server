package org.games.gate.cmd;

import org.games.cmd.CommandHandler;
import org.games.constant.CommandType;

public interface HandlerFinder {
    CommandHandler find(CommandType type);
}

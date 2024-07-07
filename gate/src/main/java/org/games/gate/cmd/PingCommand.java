package org.games.gate.cmd;

import org.games.cmd.Command;
import org.games.cmd.CommandHeader;
import org.games.constant.CommandType;
import org.games.gate.codec.CommandHeaderInfo;

public class PingCommand implements Command {
    public final CommandHeaderInfo header;
    public PingCommand(CommandHeader header) {
        this.header = (CommandHeaderInfo)header;
    }

    @Override
    public CommandType type() {
        return CommandType.PING;
    }

}

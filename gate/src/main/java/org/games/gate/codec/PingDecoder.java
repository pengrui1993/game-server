package org.games.gate.codec;

import org.games.cmd.Command;
import org.games.cmd.CommandDecoder;
import org.games.cmd.CommandHeader;
import org.games.constant.CommandType;
import org.games.gate.cmd.PingCommand;
import org.springframework.stereotype.Component;

@Component
public class PingDecoder implements CommandDecoder {
    @Override
    public CommandType type() {
        return CommandType.PING;
    }
    @Override
    public Command encode(CommandHeader header, byte[] body) {
        final PingCommand cmd;
        parse(cmd = new PingCommand(header));
        return cmd;
    }
    private void parse(PingCommand cmd){
        CommandHeaderInfo header = cmd.header;
        byte[] body = header.bodyData;
    }
}

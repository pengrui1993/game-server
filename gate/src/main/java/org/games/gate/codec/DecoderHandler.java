package org.games.gate.codec;

import org.games.cmd.Command;
import org.games.cmd.CommandDecoder;
import org.games.cmd.CommandHeader;
import org.games.constant.CommandType;
import org.games.gate.App;

public interface DecoderHandler {
    Command decode(CommandHeader header, byte[] decoded);

    static CommandDecoder getCommandDecoder(CommandHeader header) {
        for (CommandDecoder decoder : App.ctx().getBean(App.class).gets(CommandDecoder.class)) {
            if(decoder.type().code==header.cmd()){
                return decoder;
            }
        }
        return new CommandDecoder() {
            @Override
            public CommandType type() {
                return CommandType.NULL;
            }
            @Override
            public Command encode(CommandHeader header, byte[] body) {
                return Command.NULL;
            }
        };
    }
}

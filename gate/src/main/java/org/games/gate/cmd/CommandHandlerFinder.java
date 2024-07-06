package org.games.gate.cmd;

import org.games.cmd.CommandContext;
import org.games.cmd.CommandHandler;
import org.games.constant.CommandType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public interface CommandHandlerFinder {
    CommandHandler find(CommandType type);

    Map<CommandType,CommandHandler> map = new HashMap<>(){{
        put(CommandType.NULL, ctx -> {
            //log.warning
        });
        put(CommandType.PING,new PongHandler());
    }};
    @Component
    class CommandHandlerFinderImpl implements CommandHandlerFinder{

        @Override
        public CommandHandler find(CommandType type) {
            return map.get(type);
        }
    }
}

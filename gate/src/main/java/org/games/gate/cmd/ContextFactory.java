package org.games.gate.cmd;

import org.games.cmd.Command;
import org.games.cmd.CommandContext;
import org.games.constant.CommandType;
import org.games.gate.session.Session;
import org.games.gate.evt.GateEventEmitter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public interface ContextFactory {
    CommandContext factory(Command cmd,Object... params);
    Map<CommandType, MyFunction> map = new HashMap<>(){{
        put(CommandType.PING, (cmd,params) -> new PingContext((PingCommand)cmd,(Session)params[0],(GateEventEmitter)params[1]));
    }};
    interface MyFunction{
        CommandContext apply(Command cmd,Object... params);
    }
    @Component
    class ContextFactoryImpl implements ContextFactory {
        @Override
        public CommandContext factory(Command cmd, Object... params) {
            return
                    map.get(cmd.type()).apply(cmd,params);
        }
    }
}

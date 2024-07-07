package org.games.gate.cmd;

import jakarta.annotation.Resource;
import org.games.cmd.CommandHandler;
import org.games.constant.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HandlerManager implements HandlerFinder {
    static final Logger log = LoggerFactory.getLogger(HandlerManager.class);
    @Resource
    private List<CommandHandler> handlers;
    @Override
    public CommandHandler find(CommandType type) {
        for (CommandHandler handler : handlers) {
            if(type==handler.type())
                return handler;
        }
        return  ctx -> log.warn("{} no handler to process", ctx.getCommand().type());
    }
}

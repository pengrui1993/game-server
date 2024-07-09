package org.games.gate.cmd;

import org.games.cmd.CommandContext;
import org.games.cmd.CommandHandler;
import org.games.gate.session.Session;

class CommandContextImpl implements CommandContext {
    Session session;
    CommandHandler handler;
    public CommandContextImpl(Session session){
        this.session = session;
        handler = (cmd,ctx)->{};
    }
}

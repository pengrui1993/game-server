package org.games.cmd;

import org.games.constant.CommandType;

public interface Command {
    CommandType type();
    default long now(){ return System.currentTimeMillis();}

    Command NULL = ()->CommandType.NULL;
}

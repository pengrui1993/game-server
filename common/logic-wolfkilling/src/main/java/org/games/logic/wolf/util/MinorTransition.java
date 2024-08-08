package org.games.logic.wolf.util;

import org.games.logic.wolf.core.Minor;

public enum MinorTransition {

    ;
    public final Minor from;
    public final Minor to;
    MinorTransition(Minor f, Minor t){
        from = f;
        to = t;
    }
}

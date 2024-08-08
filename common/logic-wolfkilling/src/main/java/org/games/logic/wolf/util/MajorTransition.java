package org.games.logic.wolf.util;

import org.games.logic.wolf.core.Major;

public enum MajorTransition {

    ;
    public final Major from;
    public final Major to;
    MajorTransition(Major f, Major t){
        from = f;
        to = t;
    }
}

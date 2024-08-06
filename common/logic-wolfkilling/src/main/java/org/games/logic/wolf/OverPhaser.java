package org.games.logic.wolf;

import org.games.logic.wolf.core.Major;

class OverPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.OVER;
    }
    private final WolfKilling ctx;
    OverPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
}

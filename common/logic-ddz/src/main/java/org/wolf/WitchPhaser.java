package org.wolf;

import org.wolf.core.Final;

class WitchPhaser extends MajorPhaser {
    @Final
    private boolean firstTimes;
    @Override
    public Major state() {
        return Major.WITCH;
    }
    private final WolfKilling ctx;
    WitchPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
    }
}

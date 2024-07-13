package org.wolf;

import org.wolf.race.Context;
import org.wolf.race.MinorPhaser;

class RacePhaser extends MajorPhaser implements Context {
    @Override
    public Major state() {
        return Major.RACE;
    }
    MinorPhaser cur;
    private final WolfKilling ctx;
    RacePhaser(WolfKilling ctx) {
        this.ctx = ctx;
        this.cur = MinorPhaser.init(Context.class.cast(this));
        cur.begin();
    }
    @Override
    public void update(float dt) {
        cur.update(dt);
    }
    @Override
    public MinorPhaser cur() {
        return cur;
    }
    @Override
    public void cur(MinorPhaser s) {
        this.cur = s;
    }
}

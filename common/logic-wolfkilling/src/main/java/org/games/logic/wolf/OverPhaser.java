package org.games.logic.wolf;

import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.role.Team;

class OverPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.OVER;
    }
    private final WolfKilling ctx;
    OverPhaser(WolfKilling ctx) {
        this.ctx = ctx;
        overDone = false;
    }
    boolean overDone;
    @Override
    public void update(float dt) {
        if(!overDone){
            ctx.changeState(new DonePhaser(ctx));
            overDone = true;
        }
    }
    @Override
    public void end() {
        super.end();
        out.println("over phaser.");
    }
}

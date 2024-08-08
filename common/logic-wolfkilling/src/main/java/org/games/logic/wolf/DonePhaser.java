package org.games.logic.wolf;

import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.role.Team;

class DonePhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.DONE;
    }
    private final WolfKilling ctx;
    DonePhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    @Override
    public void begin() {
        ctx.winner = ctx.teamAllDied(Team.WLD)?Team.KIND:Team.WLD;
        out.println("done begin.");
    }
    @Override
    public void update(float dt) {
        ctx.setGameOver();
    }
    @Override
    public void end() {
        out.println("done end,status:"+ctx.getRoles());
    }
}

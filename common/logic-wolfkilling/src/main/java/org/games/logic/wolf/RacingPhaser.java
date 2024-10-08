package org.games.logic.wolf;

import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.race.Context;
import org.games.logic.wolf.core.Minor;
import org.games.logic.wolf.race.MinorPhaser;

import java.util.List;

class RacingPhaser extends MajorPhaser implements Context {
    @Override
    public Major state() {
        return Major.RACE;
    }
    @Override
    public Minor minor() {
        return cur.state();
    }

    @Override
    public String speaker() {
        return cur.speaker();
    }

    MinorPhaser cur;
    private final WolfKilling ctx;
    RacingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
        this.cur = MinorPhaser.init(this);
        cur.begin();
    }
    String sergeant;
    @Override
    public void end() {
        if(cur.state()==Minor.DONE){
            ctx.setSergeant(sergeant);
            cur.end();
        }else{
            out.println("racing phaser end , should be Minor.DONE but not");
        }
    }
    @Override
    public void update(float dt) {
        Context.super.onTick(dt);
    }

    @Override
    public void event(int type, Object... params) {
        cur.event(type, params);
    }

    @Override
    public MinorPhaser cur() {
        return cur;
    }
    @Override
    public void cur(MinorPhaser s) {
        this.cur = s;
    }
    @Override
    public WolfKilling top() {
        return ctx;
    }
    @Override
    public List<String> joinedUsers() {
        return ctx.getJoinedUsers();
    }
    @Override
    public void setSergeant(String sergeant) {
        if(cur.state()==Minor.DONE){
            this.sergeant = sergeant;
            ctx.changeState(new CalcDiedPhaser(ctx));
        }else{
            out.println("invalid state to set sergeant");
        }
    }
}

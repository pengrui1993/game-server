package org.games.logic.wolf;

import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.role.Roles;
import org.games.logic.wolf.role.impl.Witch;
import org.games.logic.wolf.util.CalcContext;

import java.util.Objects;
import java.util.function.Consumer;

class CalcDiedPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.CALC_DIED;
    }
    private final WolfKilling ctx;
    CalcDiedPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    boolean calcDone;
    @Override
    public void update(float dt) {
        if(!calcDone){
            handle();
            calcDone = true;
        }
    }
    void handle(){
        final CalcContext cc = ctx.calcCtx;
        if(cc.isWitchInvalidOperation()){
            out.println("invalid witch action both of saved and killed");
            ctx.changeState(new OverPhaser(ctx));
            return;
        }
        label:
        {
            if(cc.isWolfEmptyKilling()){  //no anyone died
                out.println("calc,wolf kill nil");
                cc.calcDiedUserId = null;
                cc.calcDiedUserIdByWitch = cc.drugKilledUserId;
                break label;
            }
            if(cc.isDoubleSaved()){ // medicine and protected same one
                out.println("calc,double saving");
                cc.calcDiedUserId = cc.protectedUserId;
                cc.calcDiedUserIdByWitch = null;
                break label;
            }
            if(cc.isProtectSaved()&&cc.isWitchKilled()){
                out.println("calc,protected and witch-killed");
                cc.calcDiedUserId = Objects.equals(cc.protectedUserId,cc.killingTargetUserId)?null:cc.killingTargetUserId;
                cc.calcDiedUserIdByWitch = cc.drugKilledUserId;
                ctx.get(Roles.WITCH).castTo(Witch.class).drug = false;
                break label;
            }
            if(cc.isProtectSaved()&&!cc.isWitchKilled()){
                out.println("calc,protected and none witch-killed");
                cc.calcDiedUserId = cc.protectedUserId;
                cc.calcDiedUserIdByWitch = null;
                break label;
            }
            if(!cc.isProtectSaved()&&cc.isWitchKilled()){
                out.println("calc,no protected and witch-killed");
                cc.calcDiedUserId = cc.killingTargetUserId;
                cc.calcDiedUserIdByWitch = cc.drugKilledUserId;
                ctx.get(Roles.WITCH).castTo(Witch.class).drug = false;
                break label;
            }
            if(cc.isWitchSaved()&&!cc.isProtectSaved()){
                out.println("calc,witch-saved and none protected");
                ctx.get(Roles.WITCH).castTo(Witch.class).medicine = false;
                cc.calcDiedUserId = null;
                cc.calcDiedUserIdByWitch = null;
                break label;
            }
            out.println("calc,"+cc.killingTargetUserId +" died");
            cc.calcDiedUserId = cc.killingTargetUserId;
            cc.calcDiedUserIdByWitch = null;
        }
        Consumer<String> goDied = (died)->{
            if(Objects.nonNull(died)){
                ctx.get(died).goDied();
                if(cc.isWolfWitchKillSame()){
                    ctx.deadInfo.addDiedInfoByWolfWitch(died,ctx.day());
                    return;
                }
                if(Objects.equals(cc.calcDiedUserId,died)){
                    ctx.deadInfo.addDiedInfoByWolf(died,ctx.day());
                    return;
                }
                if(cc.isDoubleSaved()){
                    ctx.deadInfo.addDiedInfoByProtector(died,ctx.day());
                }else{
                    ctx.deadInfo.addDiedInfoByWitch(died,ctx.day());
                }
            }
        };
        goDied.accept(cc.calcDiedUserId);
        goDied.accept(cc.calcDiedUserIdByWitch);
        ctx.changeState(ctx.anyTeamWinner()?new OverPhaser(ctx):new PublishDiedInfoPhaser(ctx));
    }
    @Override
    public void begin() {
        out.println("calc died,begin");
        calcDone = false;
    }
    @Override
    public void end() {
        out.println("calc died,end");
        //MajorPhaser cur = ctx.cur();
        //System.out.println(cur);//CalcDiedPhaser.class
    }
}

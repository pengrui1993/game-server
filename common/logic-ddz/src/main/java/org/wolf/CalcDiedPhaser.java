package org.wolf;

import org.wolf.role.Roles;
import org.wolf.role.impl.Witch;
import org.wolf.util.CalcContext;

class CalcDiedPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.CALC_DIED;
    }
    private final WolfKilling ctx;
    CalcDiedPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }

    @Override
    public void begin() {
        out.println("calc died,begin");
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
                cc.calcDiedUserId = cc.protectedUserId;
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
        ctx.changeState(new PublishDiedInfoPhaser(ctx));
    }
    @Override
    public void end() {
        out.println("calc died,end");
        //MajorPhaser cur = ctx.cur();
        //System.out.println(cur);//CalcDiedPhaser.class
    }
}

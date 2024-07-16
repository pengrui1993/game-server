package org.wolf;

import org.wolf.role.Roles;
import org.wolf.util.CalcContext;

import java.util.Objects;

class CalcActionPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.CALC_ACTION;
    }
    private final WolfKilling ctx;
    CalcActionPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }

    @Override
    public void begin() {
        final CalcContext cc = ctx.calcCtx;
        if(cc.isWitchInvalidOperation()){
            out.println("invalid witch action both of saved and killed");
            ctx.changeState(new OverPhaser(ctx));
            return;
        }
        if(cc.isWolfEmptyKilling()){  //no anyone died
            out.println("wolf kill nil");
            cc.calcDiedUserId = null;
            ctx.changeState(new PublishDiedInfoPhaser(ctx));
            return;
        }
        if(cc.isDoubleSaved()){ // medicine and protected same one
            out.println(cc.protectedUserId+" died cause by save and protecte same one");
            cc.calcDiedUserId = cc.protectedUserId;
            if(ctx.get(cc.protectedUserId).role()== Roles.HUNTER){
                cc.hunterAction = true;
                out.println("killed hunter");
                ctx.changeState(new HunterPhaser(ctx));
                return;
            }
            ctx.changeState(new PublishDiedInfoPhaser(ctx));
            return;
        }
        final boolean protectorWorking = Objects.equals(cc.killingTargetUserId,cc.protectedUserId);
        if(protectorWorking){
            cc.calcDiedUserId = null;
            ctx.changeState(new PublishDiedInfoPhaser(ctx));
            out.println("protector protected will be killed user");
        }else{
            cc.calcDiedUserId = cc.killingTargetUserId;
            ctx.changeState(new PublishDiedInfoPhaser(ctx));
            out.println(cc.killingTargetUserId +" died");
        }
    }
    @Override
    public void end() {
        MajorPhaser cur = ctx.cur();
        System.out.println(cur);
    }
}

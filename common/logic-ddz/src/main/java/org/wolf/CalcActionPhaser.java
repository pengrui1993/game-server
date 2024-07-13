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
        if(Objects.nonNull(cc.drugKilledUserId)&&Objects.nonNull(cc.medicineSavedUserId)){
            out.println("invalid witch action both of saved and killed");
            ctx.changeState(new OverPhaser(ctx));
            return;
        }
        if(Objects.equals(cc.protectedUserId,cc.medicineSavedUserId)
            && Objects.nonNull(cc.protectedUserId)
        ){ // medicine and protected same one
            out.println(cc.protectedUserId+" died cause by save and protecte same one");
            if(ctx.roles.get(cc.protectedUserId).role()== Roles.HUNTER){
                cc.hunterAction = true;
                ctx.changeState(new HunterPhaser(ctx));
            }else{
                ctx.changeState(new PublishDiedInfoPhaser(ctx));
            }
            return;
        }
        if(Objects.isNull(cc.killingTargetUserId)){  //no anyone died
            out.println("wolf kill nil");
            cc.calcDiedUserId = null;
            ctx.changeState(new PublishDiedInfoPhaser(ctx));
            return;
        }
        if(Objects.equals(cc.killingTargetUserId,cc.protectedUserId)){
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

    }
}

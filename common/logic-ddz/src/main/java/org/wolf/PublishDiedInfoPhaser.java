package org.wolf;


import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.role.Roles;
import org.wolf.role.impl.Hunter;
import org.wolf.util.CalcContext;


class PublishDiedInfoPhaser extends MajorPhaser{
    @Override
    public Major state() {
        return Major.DIED_INFO;
    }
    private final WolfKilling ctx;
    PublishDiedInfoPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    float last;
    @Final
    boolean first;
    boolean test;
    @Override
    public void begin() {
        last = 0;
        first = ctx.dayNumber<1;
        out.println("publish info phaser begin");
        test = false;
    }
    @Override
    public void end() {
        final CalcContext cc = ctx.calcCtx;
        out.println("publish,"+cc.calcDiedUserId+","+cc.calcDiedUserIdByWitch);
    }
    private void change(){
        final CalcContext cc = ctx.calcCtx;
        final Runnable complexPhaser = ()->{
            out.println("killed hunter");
            ctx.changeState(new LastWordsPhaser(ctx,cc.calcDiedUserId,()-> ctx.changeState(new HunterPhaser(ctx,()->{
                Hunter hunter = ctx.get(Roles.HUNTER).castTo(Hunter.class);
                if(hunter.killed()){
                    ctx.changeState(new LastWordsPhaser(ctx, hunter.killedUserId, ()-> ctx.changeState(new OrderingPhaser(ctx))));
                }else{
                    ctx.changeState(new OrderingPhaser(ctx));
                }
            }))));
        };
        final Runnable simplePhaser = ()->{
            if(!cc.isTargetDied()){
                ctx.changeState(new OrderingPhaser(ctx));
                return;
            }
            ctx.changeState(new LastWordsPhaser(ctx,cc.calcDiedUserId,()->ctx.changeState(new OrderingPhaser(ctx))));
        };
        final Roles role = ctx.get(cc.calcDiedUserId).role();
        if(ctx.setting.hunterAbilityWhenWolfKill){
            if(cc.isTargetDied()&&role==Roles.HUNTER){
                complexPhaser.run();
                return;
            }
            simplePhaser.run();
        }else{
            if(first&&cc.isTargetDied()&&role== Roles.HUNTER){
                complexPhaser.run();
                return;
            }
            simplePhaser.run();
        }
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>5||test){
            change();
        }
    }

    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1)return;
                switch (Action.from(Integer.class.cast(params[0]))){
                    case UNKNOWN -> {

                    }
                    case TEST_DONE -> {
                        test = true;
                        out.println("publish died info, test enabled");
                    }

                }
            }
        }
    }
}

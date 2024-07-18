package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.role.Roles;
import org.wolf.role.impl.Witch;

import java.util.Objects;

class WitchPhaser extends MajorPhaser {
    @Final
    private boolean firstTimes;
    @Final
    private Witch witch;
    private String witchId;
    @Override
    public Major state() {
        return Major.WITCH;
    }
    private final WolfKilling ctx;
    WitchPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    enum State{
        SAVING,KILLING
    }
    State state;
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
        witch = ctx.get(Roles.WITCH, Witch.class);
        witchId = ctx.getId(Roles.WITCH);
        state = Objects.nonNull(ctx.calcCtx.killingTargetUserId)&&witch.hasMedicine()
                    ?State.SAVING:State.KILLING;
        out.println("enter witch phaser,first times:"+firstTimes);

    }
    private void onWitchAction(Object... params){
        if(params.length < 3) {
            out.println("witch kill action mission action sender");
            return;
        }
        String sender = String.class.cast(params[1]);
        if(!Objects.equals(witchId,sender)){
            out.println("cannot match witch user id");
            return;
        }
        final Runnable change = ()->{
            if(firstTimes){
                ctx.changeState(new PredictorPhaser(ctx));
                return;
            }
            if(ctx.get(Roles.PROTECTOR).alive()){
                ctx.changeState(new ProtectorPhaser(ctx));
                return;
            }
            ctx.changeState(new CalcDiedPhaser(ctx));
        };
        String action = String.class.cast(params[2]);
        switch (action){
            case "save"->{
                if(state!=State.SAVING){
                    out.println("witch,state is not saving ,cannot do save");
                    return;
                }
                out.println("on witch save");
                ctx.calcCtx.medicineSavedUserId = ctx.calcCtx.killingTargetUserId;
                witch.medicine = false;
                change.run();
            }
            case "kill"->{
                if(params.length<4){
                    out.println("missing kill target");
                    return;
                }
                String target = String.class.cast(params[3]);
                if(-1==ctx.index(target)){
                    out.println("invalid target of killing with witch ,target:"+target);
                    return;
                }
                if(!ctx.get(target).alive()){
                    out.println("target already died");
                    return;
                }
                out.println("on witch kill");
                ctx.calcCtx.drugKilledUserId = target;
                witch.drug = false;
                change.run();
            }
            case "cancel"-> {
                out.println("on witch cancel");
                if(state==State.SAVING){
                    ctx.calcCtx.medicineSavedUserId = null;
                    if(witch.hasDrug())state = State.KILLING;
                    else change.run();
                    return;
                }
                change.run();
            }
            default -> out.println("unknown action in witch phaser:"+action);
        }
    }
    @Override
    public void event(int type, Object... params) {
        if (params.length < 1) {
            out.println("event:witch mission action type");
            return;
        }
        if (Event.from(type) == Event.ACTION
            &&Action.from(Integer.class.cast(params[0])) == Action.WITCH_ACTION
        ) {
            onWitchAction(params);
        }
    }
}

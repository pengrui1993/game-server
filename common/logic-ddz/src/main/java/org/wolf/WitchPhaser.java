package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.role.Role;
import org.wolf.role.Roles;
import org.wolf.role.Witch;

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
    }
    private void onWitchAction(Object... params){
        if(params.length < 3) {
            out.println("witch kill action mission action sender");
            return;
        }
        String who = String.class.cast(params[1]);
        String action = String.class.cast(params[2]);
        if(!Objects.equals(witchId,who)){
            out.println("cannot match witch user id");
            return;
        }
        final Runnable change = ()->{
            if(firstTimes){
                ctx.changeState(new PredictorPhaser(ctx));
                return;
            }
            boolean protectorCanAction = ctx.get(Roles.PROTECTOR).alive();
            if(protectorCanAction){
                ctx.changeState(new ProtectorPhaser(ctx));
            }else{
                ctx.changeState(new CalcActionPhaser(ctx));
            }
        };
        switch (action){
            case "save"->{
                if(state!=State.SAVING)return;
                ctx.calcCtx.medicineSavedUserId = ctx.calcCtx.killingTargetUserId;
                out.println("do saving");
                if(witch.hasDrug())state = State.KILLING;
                else change.run();
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
                ctx.calcCtx.drugKilledUserId = target;
                change.run();
            }
            case "cancel"-> {
                if(state==State.SAVING){
                    ctx.calcCtx.medicineSavedUserId = null;
                    if(witch.hasDrug())state = State.KILLING;
                    else change.run();
                }else{
                    change.run();
                }
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

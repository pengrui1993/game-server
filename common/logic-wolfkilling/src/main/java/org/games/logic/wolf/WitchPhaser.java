package org.games.logic.wolf;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.role.Roles;
import org.games.logic.wolf.role.impl.Witch;

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
    float last;
    float curLast;
    float limit;
    boolean needChange;
    boolean innerStateChange;
    boolean requestedCancel;

    @Override
    public void update(float dt) {
        last+=dt;
        curLast+=dt;
        Runnable cancelAction = ()->{
            if(firstTimes){
                ctx.calcCtx.medicineSavedUserId = null;
                needChange = true;
                return;
            }
            switch (state){
                case SAVING -> {
                    ctx.calcCtx.medicineSavedUserId = null;
                    innerStateChange = true;
                }
                case KILLING ->{
                    ctx.calcCtx.drugKilledUserId = null;
                    needChange = true;
                }
            }
        };
        if(requestedCancel){
            cancelAction.run();
            requestedCancel = false;
        }else if(curLast>=limit){
            cancelAction.run();
        }
        if(innerStateChange){
            if(witch.hasDrug()){
                state = State.KILLING;
                curLast = 0;
                limit = ctx.setting.witchKillingActionTimeoutLimit;
            }else needChange = true;
            innerStateChange = false;
        }
        if(needChange){
            change();
            needChange = false;
        }
    }
    void change() {
        if(firstTimes){
            ctx.changeState(new PredictorPhaser(ctx));
            return;
        }
        if(ctx.get(Roles.PROTECTOR).alive()){
            ctx.changeState(new ProtectorPhaser(ctx));
            return;
        }
        ctx.changeState(new CalcDiedPhaser(ctx));
    }
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
        witch = ctx.get(Roles.WITCH, Witch.class);
        witchId = ctx.getId(Roles.WITCH);
        state = Objects.nonNull(ctx.calcCtx.killingTargetUserId)
                    &&witch.hasMedicine()
                        ?State.SAVING
                        :State.KILLING;
        last = 0;
        curLast = 0;
        limit = witch.hasMedicine()
                ?ctx.setting.witchSavingActionTimeoutLimit
                :ctx.setting.witchKillingActionTimeoutLimit;
        innerStateChange = false;
        out.println("enter witch phaser,first times:"+firstTimes);
    }
    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> out.println("witch null");
            case ACTION -> {
                if (params.length < 1) {
                    out.println("event:witch mission action type");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case TEST_DONE -> {
                        out.println("witch phaser test no impl");
                    }
                    case WITCH_ACTION -> {
                        if(params.length < 3) {
                            out.println("witch kill action mission action sender");
                            return;
                        }
                        String sender = String.class.cast(params[1]);
                        if(!Objects.equals(witchId,sender)){
                            out.println("cannot match witch user id");
                            return;
                        }
                        String actionType = String.class.cast(params[2]);
                        switch (actionType){
                            case "save"->{
                                if(state!=State.SAVING){
                                    out.println("witch,state is not saving ,cannot do save");
                                    return;
                                }
                                ctx.calcCtx.medicineSavedUserId = ctx.calcCtx.killingTargetUserId;
                                witch.medicine = false;
                                needChange = true;
                                ctx.sessions.notifyWitchSave(witchId);
                            }
                            case "kill"->{
                                if(state!=State.KILLING){
                                    out.println("witch,state is not killing ,cannot do kill");
                                    return;
                                }
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
                                witch.drug = false;
                                needChange = true;
                                ctx.sessions.notifyWitchKill(witchId,target);
                            }
                            case "cancel"-> {
                                requestedCancel = true;
                                ctx.sessions.notifyWitchCancel(witchId);
                            }
                            default -> out.println("unknown action in witch phaser:"+actionType);
                        }
                        out.println("witch phaser action:"+actionType);
                    }
                }
            }
        }
    }
}

package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.core.Once;
import org.wolf.evt.Event;
import org.wolf.role.Predictor;
import org.wolf.role.Roles;
import org.wolf.role.Witch;

import java.util.Map;
import java.util.Objects;

class PredictorPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.PREDICTOR;
    }
    private final WolfKilling ctx;
    PredictorPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }

    @Once  //userId wolf , yes or no
    Map.Entry<String,Boolean> verify;
    @Final
    boolean firstTimes;
    @Final
    String predictorUserId;
    @Final
    Predictor predictor;
    float last;
    float limit;
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
        predictorUserId = ctx.getId(Roles.PREDICTOR);
        predictor = ctx.get(Roles.PREDICTOR).castTo(Predictor.class);
        last = 0;
        limit = ctx.setting.predictorActionTimeoutLimit;
    }
    @Override
    public void end() {
        out.println("predictor phaser exit,result:"+verify);
    }
    @Override
    public void update(float dt) {
        Runnable change = ()->{
            if(firstTimes){
                ctx.changeState(new RacingPhaser(ctx));
                return;
            }
            Witch witch = ctx.get(Roles.WITCH).castTo(Witch.class);
            if(witch.alive()&&witch.hasAnyMedicine()){
                ctx.changeState(new WitchPhaser(ctx));
            }else if(ctx.get(Roles.PROTECTOR).alive()){
                ctx.changeState(new ProtectorPhaser(ctx));
            }else{
                ctx.changeState(new CalcDiedPhaser(ctx));
            }
        };
        last+=dt;
        if(Objects.nonNull(verify)
                ||last>=limit){
            change.run();
        }
    }
    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1){
                    out.println("action on predictor phrase require 3 params(action,who,verify)");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case PREDICTOR_ACTION->{
                        String sender = String.class.cast(params[1]);
                        if(!Objects.equals(sender,predictorUserId)){
                            out.println("action is not predictor,userId:"+sender);
                            return;
                        }
                        String target = String.class.cast(params[2]);
                        verify = Map.entry(target,Roles.WOLF==ctx.get(target).role());
                        //notify
                        out.println("predictor phaser,verify "+verify.getKey()+" result:"+verify.getValue());
                        predictor.ifIsThen(org.wolf.role.impl.Predictor.class
                                ,p-> p.verifies.put(verify.getKey(),verify.getValue()));
                    }
                    case UNKNOWN -> {}
                }
            }
        }
    }
}

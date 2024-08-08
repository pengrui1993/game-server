package org.games.logic.wolf;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.role.impl.Predictor;
import org.games.logic.wolf.role.Roles;
import org.games.logic.wolf.role.Witch;

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
    org.games.logic.wolf.role.Predictor predictor;
    float last;
    float limit;
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
        predictorUserId = ctx.getId(Roles.PREDICTOR);
        predictor = ctx.get(Roles.PREDICTOR).castTo(org.games.logic.wolf.role.Predictor.class);
        last = 0;
        limit = ctx.setting.predictorActionTimeoutLimit;
        out.println("predictor phaser begin.");
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
                        ctx.sessions.notifyPredictorPredictor(verify);
                        predictor.ifIsThen(Predictor.class
                                ,p-> p.verifies.put(verify.getKey(),verify.getValue()));
                    }
                    case UNKNOWN -> {}
                }
            }
        }
    }
}

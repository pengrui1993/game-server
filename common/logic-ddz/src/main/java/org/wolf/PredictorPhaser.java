package org.wolf;

import org.wolf.core.Final;

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
    //userId is wolf , yes or no
    Map.Entry<String,Boolean> verify;
    @Final
    boolean firstTimes;
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
    }

    @Override
    public void end() {
        out.println("verify "+verify.getKey()+" result:"+verify.getValue());
    }

    @Override
    public void update(float dt) {
        if(Objects.nonNull(verify.getValue())){
            if(firstTimes){
                ctx.changeState(new RacePhaser(ctx));
            }else{

            }
        }
    }
}

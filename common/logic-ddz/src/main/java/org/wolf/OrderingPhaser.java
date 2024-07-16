package org.wolf;

import org.wolf.action.Action;
import org.wolf.evt.Event;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

class OrderingPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.ORDERING;
    }
    private final WolfKilling ctx;
    OrderingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    Boolean orderingCCW;
    float last;
    @Override
    public void begin() {
        last = 0;
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=10&&Objects.isNull(orderingCCW)){
            orderingCCW = ThreadLocalRandom.current().nextBoolean();
        }
        if(Objects.nonNull(orderingCCW)){
            ctx.changeState(new TalkingPhaser(ctx,orderingCCW));
        }
    }

    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case ACTION -> {
                if(params.length<3){
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case ORDERING_DECISION -> {

                    }
                }
            }
        }
    }
}

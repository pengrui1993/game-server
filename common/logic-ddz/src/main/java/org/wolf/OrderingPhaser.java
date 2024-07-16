package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
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
    @Final
    boolean first;
    @Final String sergeant;
    @Final float limit;
    @Override
    public void begin() {
        last = 0;
        limit = ctx.setting.orderingLimit;
        first = ctx.dayNumber<1;
        sergeant = ctx.sergeant;
        if(Objects.isNull(sergeant))
            orderingCCW = ThreadLocalRandom.current().nextBoolean();
        out.println("ordering phaser,ccw:"+orderingCCW+", sergeant:"+sergeant);
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit&&Objects.isNull(orderingCCW)){
            orderingCCW = ThreadLocalRandom.current().nextBoolean();
        }
        if(Objects.nonNull(orderingCCW)){
            ctx.talkingOrderingCCW = orderingCCW;
            ctx.changeState(new TalkingPhaser(ctx));
        }
    }

    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case ACTION -> {
                if(params.length<1){
                    out.println("ordering phaser ,required more then one params");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case ORDERING_DECISION -> {
                        if(params.length<3){
                            out.println("ordering phaser ,required 3 params");
                            return;
                        }
                        String sender = String.class.cast(params[1]);
                        if(!Objects.equals(sender,sergeant)){
                            out.println("must be sergeant send action");
                            return;
                        }
                        orderingCCW =Boolean.parseBoolean(String.class.cast(params[2]));
                    }
                    case TEST_DONE -> {
                        orderingCCW = ThreadLocalRandom.current().nextBoolean();
                        out.println("ordering test enabled");
                    }
                }
            }
            case NULL -> {}
        }
    }
}

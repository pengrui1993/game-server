package org.wolf;

import java.util.Objects;

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

    @Override
    public void begin() {
        last = 0;
    }

    @Override
    public void update(float dt) {
        last+=dt;

        if(last>5){
            ctx.changeState(new OrderingPhaser(ctx));
        }
    }
}

package org.wolf;

class HunterPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.HUNTER;
    }
    private final WolfKilling ctx;
    HunterPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    @Override
    public void end() {
    }
    private void nextPhaser(){
        if(ctx.calcCtx.hunterAction){
//                ctx.changeState(new );
            ctx.calcCtx.hunterAction = false;
        }
    }
    float last;

    @Override
    public void begin() {
        last=0;
    }

    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=10){
            nextPhaser();
        }
    }
    @Override
    public void event(int type, Object... params) {

    }
}

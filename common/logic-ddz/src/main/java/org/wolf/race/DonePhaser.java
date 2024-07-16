package org.wolf.race;
/*
for sergeant
 */
class DonePhaser extends MinorPhaser{
    private final Context ctx;
    private final String sergeant;
    public DonePhaser(Context ctx, String sergeant) {
        this.ctx = ctx;
        this.sergeant = sergeant;
    }
    @Override
    public Minor state() {
        return Minor.DONE;
    }
    boolean called;
    @Override
    public void begin() {
        called = false;
        out.println("DonePhaser.begin");
    }

    @Override
    public void end() {
        out.println("done of race,sergeant:"+sergeant);
    }

    @Override
    public void update(float dt) {
        if(!called){
            ctx.setSergeant(sergeant);
            called = true;
        }
    }
}

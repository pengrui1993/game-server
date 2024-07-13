package org.wolf.race;

class HandsActionPhaser extends MinorPhaser {
    private final Context ctx;
    public HandsActionPhaser(Context ctx) {
        this.ctx = ctx;
    }
    @Override
    public Minor state() {
        return Minor.HANDS;
    }
}

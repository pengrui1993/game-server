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
}//PreparingPhaser

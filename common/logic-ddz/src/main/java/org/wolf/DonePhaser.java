package org.wolf;

class DonePhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.DONE;
    }
    private final WolfKilling ctx;
    DonePhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
}

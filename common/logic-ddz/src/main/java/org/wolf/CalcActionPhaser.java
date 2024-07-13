package org.wolf;

class CalcActionPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.CALC_ACTION;
    }
    private final WolfKilling ctx;
    CalcActionPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
}

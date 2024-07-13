package org.wolf;

class ProtectorPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.PROTECTOR;
    }
    private final WolfKilling ctx;
    ProtectorPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
}

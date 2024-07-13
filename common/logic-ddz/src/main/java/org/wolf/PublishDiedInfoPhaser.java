package org.wolf;

class PublishDiedInfoPhaser extends MajorPhaser{
    @Override
    public Major state() {
        return Major.DIED_INFO;
    }
    private final WolfKilling ctx;
    PublishDiedInfoPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
}

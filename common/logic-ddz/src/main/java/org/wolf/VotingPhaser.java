package org.wolf;

class VotingPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.VOTING;
    }
    private final WolfKilling ctx;
    VotingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
}

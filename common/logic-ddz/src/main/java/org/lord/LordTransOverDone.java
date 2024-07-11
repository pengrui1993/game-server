package org.lord;

public class LordTransOverDone extends Transition{
    public LordTransOverDone(LandLord sm) {
        super(sm,LandLord.Trans.OVER_DONE.ordinal()
                , LandLord.State.OVER.ordinal()
                , LandLord.State.DONE.ordinal()
        );
        this.sm = sm;
    }
    final LandLord sm;
    @Override
    public int trans(State src) {
        return super.trans(src);
    }
}

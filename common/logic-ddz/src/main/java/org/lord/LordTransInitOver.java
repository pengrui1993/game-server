package org.lord;

public class LordTransInitOver extends Transition{
    public LordTransInitOver(LandLord sm) {
        super(sm,LandLord.Trans.INIT_OVER.ordinal()
                , LandLord.State.INIT.ordinal()
                , LandLord.State.OVER.ordinal()
        );
        this.sm = sm;
    }
    final LandLord sm;
    @Override
    public int trans(State src) {
        return super.trans(src);
    }
}

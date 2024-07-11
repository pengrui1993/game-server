package org.lord;

public class LordTransPreparingOver extends Transition{
    public LordTransPreparingOver(LandLord sm) {
        super(sm,LandLord.Trans.PREPARING_OVER.ordinal()
                , LandLord.State.PREPARING.ordinal()
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

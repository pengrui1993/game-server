package org.lord;

public class LordTransInitPreparing extends Transition{
    final LandLord sm;
    public LordTransInitPreparing(LandLord sm) {
        super(sm,LandLord.Trans.INIT_PREPARING.ordinal()
                , LandLord.State.INIT.ordinal()
                , LandLord.State.PREPARING.ordinal()
                );
        this.sm = sm;
    }
    @Override
    public int trans(State src) {
        final int desc = super.trans(src);
        LordPreparing p = LordPreparing.class.cast(sm.gets(desc));
        sm.state =  LandLord.State.PREPARING;
        return desc;
    }

}

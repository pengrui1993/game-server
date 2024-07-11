package org.lord;

public class LordTransPlayingOver extends Transition{
    public LordTransPlayingOver(LandLord sm) {
        super(sm,LandLord.Trans.PLAYING_OVER.ordinal()
                , LandLord.State.PLAYING.ordinal()
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

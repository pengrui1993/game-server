package org.lord;

import java.util.Collections;

public class LordTransPreparingPlaying extends Transition{
    final LandLord sm;
    public LordTransPreparingPlaying(LandLord sm) {
        super(sm,LandLord.Trans.PREPARING_PLAYING.ordinal()
                , LandLord.State.PREPARING.ordinal()
                , LandLord.State.PLAYING.ordinal()
        );
        this.sm = sm;
    }
    @Override
    public int trans(State src) {
        int desc = super.trans(src);
        LordPlaying p = LordPlaying.class.cast(sm.gets(desc));
        sm.state =  LandLord.State.PREPARING;
        p.joinedUserId = Collections.unmodifiableList(sm.joinedUserId);
        return desc;
    }
}

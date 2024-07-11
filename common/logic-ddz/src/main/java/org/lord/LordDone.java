package org.lord;

public class LordDone extends State{
    public LordDone(LandLord sm) {
        super(LandLord.State.DONE.ordinal(),sm);
    }
}

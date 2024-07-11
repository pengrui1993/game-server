package org.lord;

public class LordOver extends State{
    public LordOver(LandLord sm) {
        super(LandLord.State.OVER.ordinal(),sm);
    }
}

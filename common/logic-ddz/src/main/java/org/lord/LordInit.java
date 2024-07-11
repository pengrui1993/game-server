package org.lord;

import java.util.Optional;

public class LordInit extends State{

    final LandLord sm;
    public LordInit(LandLord sm) {
        super(LandLord.State.INIT.ordinal(),sm);
        this.sm = sm;
    }
    Runnable timeoutHandler;
    Runnable ticker;

    @Override
    public void enter(Object... params) {
        last = 0;
        timeoutHandler = ()->{
            if(last>30){
                sm.trans(LandLord.State.OVER.ordinal());
                ticker = ()->{
                    System.out.println("death code");
                };
            }
        };
    }
    float last;
    @Override
    public void update(float dt) {
        last+=dt;
        ticker.run();
        Optional.ofNullable(timeoutHandler).ifPresent(Runnable::run);
    }
}

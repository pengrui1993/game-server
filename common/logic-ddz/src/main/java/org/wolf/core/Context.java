package org.wolf.core;


import java.util.Objects;
import java.util.function.Consumer;

public interface Context <PHA extends Enum<PHA>
        ,STA extends Phaser<PHA>
        ,CTX extends Context<PHA,STA,CTX>>{
    default void changeState(STA next){
        cur().end();
        next.begin();
        cur(next);
    }
    STA cur();
    void cur(STA s);
    default void onTick(float dt){cur().update(dt);}
    default void onEvent(int cmd,Object... params){cur().event(cmd,params);}
    default void sleep(long l){ sleep0(l);}
    default long now(){ return now0();}
    static long now0(){ return System.currentTimeMillis();}
    static void sleep0(long l){sleep(l,(t)->{});}
    static void sleep(long l, Consumer<Throwable> c){
        if(l<=0)return;
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            if(Objects.nonNull(c))c.accept(e);
            else throw new RuntimeException(e);
        }
    }
}

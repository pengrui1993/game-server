package org.games.logic.wolf.core;


import java.util.Objects;
import java.util.function.Consumer;

public interface Context <PHA extends Enum<PHA>
        ,STA extends Phaser<PHA>
        ,CTX extends Context<PHA,STA,CTX>>{
    @Write
    default void changeState(STA next){
//        STA tmp = cur();
        cur().end();
        cur(next);
        next.begin();
//        delete tmp
    }
    @Read
    STA cur();
    @Write
    void cur(STA s);
    @Write
    default void onTick(float dt){cur().update(dt);}
    @Write
    default void onEvent(int cmd,Object... params){cur().event(cmd,params);}
    @Write
    default void onDestroy(){cur().end();cur(null);}
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

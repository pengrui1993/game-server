package org.games.logic.wolf.core;

import java.io.PrintStream;

public interface Phaser<E extends Enum<E>> {
    PrintStream out = System.out;
    default void begin(){
        out.println(this.getClass().getSimpleName()+" no impl begin");
        Temp.show = true;
    }
    default void end(){ out.println(this.getClass().getSimpleName()+" no impl end");}
    default void update(float dt){
        if(Temp.show){
            out.println(this.getClass().getSimpleName()+" no impl update");
            Temp.show = false;
        }
    }
    class Temp{
        static boolean show;
    }
    default void event(int type,Object... params){ out.println(this.getClass().getSimpleName()+" no impl event");}
    E state();
}


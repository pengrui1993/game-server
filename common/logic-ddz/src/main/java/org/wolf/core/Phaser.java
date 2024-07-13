package org.wolf.core;

import java.io.PrintStream;

public interface Phaser<E extends Enum<E>> {
    PrintStream out = System.out;
    default void begin(){ out.println(this.getClass().getSimpleName()+"no impl begin");}
    default void end(){ out.println(this.getClass().getSimpleName()+"no impl end");}
    default void update(float dt){ out.println(this.getClass().getSimpleName()+"no impl update");}
    default void event(int type,Object... params){ out.println(this.getClass().getSimpleName()+"no impl event");}
    E state();
}


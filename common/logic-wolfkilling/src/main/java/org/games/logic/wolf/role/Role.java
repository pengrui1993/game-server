package org.games.logic.wolf.role;

import org.games.logic.wolf.core.Phaser;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public interface Role {
    PrintStream out = Phaser.out;
    Roles role();
    boolean alive();
    default boolean goDied(){return false;}
    default <T extends Role> T castTo(Class<T> clazz){
        if(clazz.isAssignableFrom(this.getClass()))return clazz.cast(this);
        return null;
    }
    default <T extends Role> void ifIsThen(Class<T> clazz, Consumer<T> c){
        if(Objects.isNull(clazz))return;
        if(Objects.isNull(c))return;
        Optional.ofNullable(castTo(clazz)).ifPresent(c);
    }
    default String info(){
        return "["+role().name()+",lived:"+alive()+"]";
    }
}

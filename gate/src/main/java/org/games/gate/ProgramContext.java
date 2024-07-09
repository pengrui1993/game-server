package org.games.gate;


import java.util.Collection;
import java.util.function.Consumer;

public interface ProgramContext{
    <T> T get(Class<T> clazz);
    <T> Collection<T> gets(Class<T> clazz);
    default <T> void postGet(Class<T> clazz,Consumer<T> c){
        post(()-> c.accept(get(clazz)));
    }
    default <T> void postGets(Class<T> clazz,Consumer<Collection<T>> c){
        post(()-> c.accept(gets(clazz)));
    }
    void exec(Runnable r);
    void post(Runnable r);
}

package org.games.support.server;


import java.util.Collection;
import java.util.function.Consumer;

public interface ProgramContext{
    <T> T get(Class<T> clazz);
    <T> Collection<T> gets(Class<T> clazz);
    void exec(Runnable r);
    void post(Runnable r);
    default <T> void postGet(Class<T> clazz,Consumer<T> c){
        post(()-> c.accept(get(clazz)));
    }
    default <T> void postGets(Class<T> clazz,Consumer<Collection<T>> c){
        post(()-> c.accept(gets(clazz)));
    }

}

package org.games.api;

public interface Notifier{

    default void sendTestEvent(){}

    default void info(){}
}

package org.games.gate;

public interface Gateway {

    void onCommanded();
    void onConnected();
    void onAuth();
    void onDisconnected();
    void onReconnected();


    void onMessaged();
    void onJoinRoom();
    void onExitRoom();
    void onRoomAction();
}

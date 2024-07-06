package org.games.gate;

public interface Gateway {
    void onCommanded();
    void onMessaged();
    void onConnected();
    void onAuth();
    void onDisconnected();
    void onReconnected();
    void onJoinRoom();
    void onExitRoom();
    void onRoomAction();
}

package org.games.gate.core.iface;

public interface GateForServiceEvent {

    void onDisconnected();
    void onReconnected();
    void onCommanded();
    void onConnected();
    void onAuth();
    void onMessaged();
    void onJoinRoom();
    void onExitRoom();
    void onRoomAction();
}

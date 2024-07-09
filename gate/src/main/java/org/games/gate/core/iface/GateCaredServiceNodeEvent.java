package org.games.gate.core.iface;

public interface GateCaredServiceNodeEvent {

    void onBusRegistered();
    void onBusUnregistered();

    void onConfigRegistered();
    void onConfigUnregistered();
    void onUsersRegistered();
    void onUsersUnregistered();
    void onAuthRegistered();
    void onAuthUnregistered();
    void onLogicsRegistered();
    void onLogicsUnregistered();



}

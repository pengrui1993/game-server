package org.games.gate.core.iface.gate;

public interface GateForCompEvent {
    void onConfigRegistered();
    void onConfigUnregistered();
    void onBusRegistered();
    void onBusUnregistered();
    void onUsersRegistered();
    void onUsersUnregistered();
    void onAuthRegistered();
    void onAuthUnregistered();
    void onLogicsRegistered();
    void onLogicsUnregistered();



}

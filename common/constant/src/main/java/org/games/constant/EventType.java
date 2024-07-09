package org.games.constant;

public enum EventType {
    NODE_CONNECT_GATE(Const.NODE_CONNECT_GATE_ID)
    ,NODE_DISCONNECT_GATE(Const.NODE_CONNECT_GATE_ID)
        ;
    public final int id;

    EventType(int id) {
        this.id = id;
    }
}

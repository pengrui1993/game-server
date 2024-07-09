package org.games.constant;

public enum EventType {

    HELLO(Const.EVT_HELLO_ID)
    , NODE_CONNECT(Const.EVT_NODE_CONNECT_ID)
    , NODE_DISCONNECT(Const.EVT_NODE_DISCONNECT_ID)
    , EVT_LOGIN_POST(Const.EVT_LOGIN_POST_ID)
        ;
    public final int id;

    EventType(int id) {
        this.id = id;
    }
}

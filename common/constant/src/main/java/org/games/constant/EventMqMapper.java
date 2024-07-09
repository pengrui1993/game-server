package org.games.constant;

public enum EventMqMapper {
    HELLO_AUTH(EventType.HELLO,MqNames.AUTH)
    ,HELLO_BUS(EventType.HELLO,MqNames.BUS)
    ,HELLO_CONFIG(EventType.HELLO,MqNames.CONFIG)
    ,HELLO_GATE(EventType.HELLO,MqNames.GATE)
    ,HELLO_LOGICS(EventType.HELLO,MqNames.LOGICS)
    ,HELLO_USERS(EventType.HELLO,MqNames.USERS)
    , NODE_CONNECT_AUTH(EventType.NODE_CONNECT,MqNames.AUTH)
    , NODE_CONNECT_BUS(EventType.NODE_CONNECT,MqNames.BUS)
    , NODE_DISCONNECT_GATE(EventType.NODE_DISCONNECT,MqNames.GATE)
    ,NODE_DISCONNECT_CONFIG(EventType.NODE_DISCONNECT,MqNames.CONFIG)
    ,EVT_LOGIN_POST_CONFIG(EventType.EVT_LOGIN_POST,MqNames.CONFIG)

    ;
    public final EventType evt;
    public final MqNames mq;

    EventMqMapper(EventType evt, MqNames mq) {
        this.evt = evt;
        this.mq = mq;
    }
}

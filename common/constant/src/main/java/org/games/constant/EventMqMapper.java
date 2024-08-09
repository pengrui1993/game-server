package org.games.constant;

public enum EventMqMapper {
    HELLO_AUTH(EventType.HELLO,MqNames.ALL)
    ,NODE_CONNECT_AUTH(EventType.NODE_CONNECT,MqNames.AUTH,MqNames.BUS)
    ,NODE_DISCONNECT_GATE(EventType.NODE_DISCONNECT,MqNames.GATE,MqNames.CONFIG)
    ,EVT_LOGIN_POST_CONFIG(EventType.EVT_LOGIN_POST,MqNames.CONFIG)
    ;
    public final EventType evt;
    public final MqNames[] mq;

    EventMqMapper(EventType evt, MqNames... mq) {
        this.evt = evt;
        this.mq = mq;
    }
}

package org.games.gate.evt;

public enum GateEventType {
   NULL
   ,COMMAND_EVENT ,NODE_EVENT
   ,TRIGGER_PING,PRE_PONG,POST_PONG, CONNECTION_ERR,SESSION_NEW
   ,CONNECTED,DISCONNECTED
   ,USER_RECONNECTED,USER_LOGIN_DONE

   ,GLOBAL_EVENT_TRIGGERED
}

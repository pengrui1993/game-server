package org.games.constant;

public enum CommandType {
    NULL(Const.CMD_NULL_ID),PING(Const.CMD_PING_ID)
    ,REQUEST_LOGIN(Const.CMD_REQ_LOGIN_ID)
    ;
    public final int code;

    CommandType(int code) {
        this.code = code;
    }
}

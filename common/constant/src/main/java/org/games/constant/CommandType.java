package org.games.constant;

public enum CommandType {
    NULL(0),PING(1);
    public final int code;

    CommandType(int code) {
        this.code = code;
    }
}

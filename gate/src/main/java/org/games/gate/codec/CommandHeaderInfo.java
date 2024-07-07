package org.games.gate.codec;

import org.games.cmd.CommandHeader;

public class CommandHeaderInfo implements CommandHeader {
    public final long decodeStartTime = now();
    public final int magic;
    public final int version;
    public final int command;
    public final int bodyLength;
    public final byte[] bodyData;
    public CommandHeaderInfo(int magic, int version, int command, int bodyLength, byte[] bodyData) {
        this.magic = magic;
        this.version = version;
        this.command = command;
        this.bodyLength = bodyLength;
        this.bodyData = bodyData;
    }
    static long now(){
        return System.currentTimeMillis();
    }
    @Override
    public int cmd() {
        return command;
    }
}

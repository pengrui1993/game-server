package org.games.event.netty;

public class NodeEventHeader {
    public int magic;
    public int version;
    public int eventId;
    public int roleId;
    public int bodyLen;

}

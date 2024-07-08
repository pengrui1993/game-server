package org.games.gate.evt;

public class QueryOtherServiceStateEvent implements GateEvent{
    public QueryOtherServiceStateEvent(int sender, int queryCommand,
                                       int queryId, long requestTime) {
    }
    @Override
    public GateEventType type() {
        return null;//TODO
    }
}

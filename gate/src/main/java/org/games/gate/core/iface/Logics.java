package org.games.gate.core.iface;



public interface Logics extends Node{
    interface GateHandler{}
    interface QueryCallback extends org.games.gate.core.QueryCallback{}
    void queryNewState(QueryCallback cb);
}

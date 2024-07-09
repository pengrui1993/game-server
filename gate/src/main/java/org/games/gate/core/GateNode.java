package org.games.gate.core;

import org.games.gate.core.iface.*;
import org.games.gate.evt.GateEvent;
import org.games.gate.ipc.GlobalEventSender;
import org.games.gate.session.Session;

import java.util.HashMap;
import java.util.Map;

public class GateNode implements Node.GateHandler
        , Logics.GateHandler
        , GlobalEventSender.GateHandler{
    int id=0;
    Map<Session,NodeDelegate> nodes = new HashMap<>();
    Auth auth;
    Bus bus;
    Config config;
    Logics logics;
    Users users;
    @Override
    public void register(Session session, Node node) {
        nodes.put(session,new NodeDelegate(node));
    }
    public Node get(Session session){
        return nodes.get(session);
    }
    static class NodeDelegate implements Node{
        Node real;
        NodeDelegate(Node node) {
            real = node;
        }
        @Override
        public Type type() {
            return real.type();
        }
        @Override
        public Session getSession() {
            return real.getSession();
        }
        @Override
        public boolean equals(Object obj) {
            if(null==obj)return false;
            if(obj instanceof Node n){
                if(this==n)return true;
                return n.equals(this);
            }
            return false;
        }
    }
    void onComponentConnected(GateEvent evt){
    }
    void onComponentDisconnected(GateEvent evt){
    }

    void onLogicsStateChanged(){
        logics.queryNewState(queryResultEvent -> {

        });
    }
}

package org.games.gate.core;

import org.games.gate.core.iface.Logics;
import org.games.gate.core.iface.Node;
import org.games.gate.evt.GateEvent;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.evt.QueryOtherServiceStateEvent;
import org.games.gate.evt.QueryResultEvent;
import org.games.gate.session.Session;

import java.util.ArrayList;
import java.util.List;

public class LogicsNode implements Logics, Node {
    private GateEventEmitter emitter;
    private Session session;
    @Override
    public Type type() {
        return Type.LOGICS;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void queryNewState(QueryCallback cb) {
        QueryContext context = new QueryContext();
        //生成一个消息id
        context.queryId = 1;//random global identity
        context.requestTime = context.now();
        context.queryCommand = 1;
        context.resultCallback = cb;
        context.sender = NodeType.GATE.id;
        contexts.add(context);
        GateEvent evt = new QueryOtherServiceStateEvent(context.sender
                , context.queryCommand
                , context.queryId
                , context.requestTime);
        emitter.emit(evt);
    }
    class QueryContext{
        int queryId;
        int sender;
        long requestTime;
        int queryCommand;
        QueryCallback resultCallback;
        long responseTime;
        long now(){return System.currentTimeMillis();}
    }
    List<QueryContext> contexts = new ArrayList<>();
    void onQueryResultEvent(QueryResultEvent event){
        int queryId = event.queryId;
        int index = -1;
        for(int i=0;i<contexts.size();i++){
            if(contexts.get(i).queryId==queryId){
                index = i;
                break;
            }
        }
        if(-1!=index){
            QueryContext qc = contexts.get(index);
            qc.responseTime = qc.now();
            qc.resultCallback
                    .accept(event.queryResult);
            contexts.remove(index);
            emitter.emit(null);//TODO emit query done event
        }
    }
}

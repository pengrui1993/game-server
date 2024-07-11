package org.lord;

import java.util.*;

public class StateMachine {
    protected State start;
    protected State end;
    protected final List<State> states = new ArrayList<>();
    protected List<Transition> transitions = new ArrayList<>();
    protected Transition curTrans;
    protected State curState;

    public StateMachine parent() {
        return null;
    }

    public void trans(int id){
        if(exited())return;
        curTrans = gett(id);
    }
    public Transition gett(int id){
        for (Transition t : transitions) {
            if(t.id==id)
                return t;
        }
        throw new IllegalStateException();
    }
    public State gets(int id){
        for (State state : states) {
            if(state.id==id)
                return state;
        }
        throw new IllegalStateException();
    }
    public StateMachine start(){
        if(null!= curState)return this;
        curState = start;
        return this;
    }
    public boolean exited(){
        return curState ==end;
    }
    public void update(float dt){
        if(curState==end)return;
        Optional.ofNullable(curTrans).ifPresent(c->{
            int id = curTrans.trans(curState);
            State newer = gets(id);
            curState.exit();
            newer.enter();
            curState = newer;
            curTrans = null;
        });
        curState.update(dt);
    }
    public void event(int id,Object... params){
        if(curState==end)return;
        Optional.ofNullable(curState).ifPresent(s->s.event(id,params));
    }
    static Queue<Map.Entry<Integer,Object>> queue = new LinkedList<>();
    public static void main(String[] args) {
        Map.Entry<Integer,Object> evt;
        StateMachine sm=new StateMachine().start();
        float fLast = 0;
        long last = now();
        final long first = last;
        while(!sm.exited()){
            //time
            long tmp = now();
            float dt = (tmp-last)/1000.f;
            fLast+=dt;
            last = tmp;
            //event
            while(null!=(evt=queue.poll()))sm.event(evt.getKey(),evt.getValue());
            //state change
            sm.update(dt);
        }
    }
    public static long now(){
        return System.currentTimeMillis();
    }
}

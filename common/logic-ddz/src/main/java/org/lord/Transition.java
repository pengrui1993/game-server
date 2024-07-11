package org.lord;

public class Transition {
    protected final StateMachine machine;
    public final int id;
    public final int srcStateId,descStateId;
    public Transition(StateMachine sm,int id,int src, int desc) {
        machine=sm;
        this.id = id;
        this.srcStateId = src;
        this.descStateId = desc;
    }
    public int trans(State src) {
        if(src.id!=srcStateId)throw new IllegalStateException();
        return descStateId;
    }
}

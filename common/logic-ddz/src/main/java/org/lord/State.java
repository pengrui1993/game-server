package org.lord;

public class State {
    public final int id;
    protected final StateMachine machine;
    public State(int id, StateMachine machine) {
        this.id = id;
        this.machine = machine;
    }

    public void update(float dt){}
    public void event(int id,Object... params){}

    public void exit(Object... params) {
    }
    public void enter(Object... params) {
    }
}

package org.lord;

import java.util.*;

public class LordPreparing extends State{
    public LordPreparing(LandLord sm) {
        super(LandLord.State.PREPARING.ordinal(), sm);
        this.sm = sm;
    }
    final LandLord sm;
    Runnable timeoutHandler;
    Runnable ticker;
    List<String> joinedUserId = new ArrayList<>();
    float last;
    float lastJoinTime;
    @Override
    public void enter(Object... params) {
        last = 0;
        timeoutHandler=()->{
            if(last>30){
                sm.trans(LandLord.State.OVER.ordinal());
            }
        };
        ticker = ()->{
            if(sm.joinedUserId.size()==3){
                sm.trans(LandLord.State.PLAYING.ordinal());
            }
        };
    }
    @Override
    public void update(float dt) {
        last+=dt;
        ticker.run();
        Optional.ofNullable(timeoutHandler).ifPresent(Runnable::run);
    }


    void onJoined(String joinUserId){
        lastJoinTime = last;
        joinedUserId.add(joinUserId);
        //notify
    }
    void onLeave(String leaveUserId){
        joinedUserId.remove(leaveUserId);
        //notify
    }
    enum Action{
        NULL,JOIN,EXIT,MESSAGE
        ;
        public static Action from(int id){
            for (Action value : Action.values()) {
                if(id==value.ordinal())
                    return value;
            }
            return Action.NULL;
        }
    }
    @Override
    public void event(int id, Object... params) {
        Action a = Action.from(id);
        switch (a){
            case JOIN -> onJoined((String)params[0]);
            case EXIT -> onLeave((String)params[0]);
        }
    }
}

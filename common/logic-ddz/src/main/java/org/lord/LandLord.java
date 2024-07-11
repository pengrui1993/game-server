package org.lord;


import java.util.*;
/*
lifecycle:created destroy start over update
client action: connected disconnected
user special action: join left message

user normal action: ask-for-lord putout pass
 */
public class LandLord extends StateMachine{
    enum State{
        INIT,PREPARING,PLAYING,OVER,DONE
    }
    enum Trans{
        INIT_PREPARING
        ,INIT_OVER
        ,PREPARING_PLAYING
        ,PREPARING_OVER
        ,PLAYING_OVER
        ,OVER_DONE
    }
    public LandLord(){
        states.addAll(List.of(start=new LordInit(this)
                , new LordPreparing(this)
                , new LordPlaying(this)
                , new LordOver(this)
                , end=new LordDone(this)
        ));
        transitions.addAll(List.of(new LordTransInitPreparing(this)
                ,new LordTransPreparingOver(this)
                ,new LordTransPreparingPlaying(this)
                ,new LordTransPlayingOver(this)
                ,new LordTransOverDone(this)
        ));
        super.start();
        createdTime = now();
    }
    State state = State.INIT;
    List<String> joinedUserId = new ArrayList<>();
    float last;
    final long createdTime;
    //*************** game lifecycle
    public void onUpdate(float dt){
        last+=dt;
        update(dt);
    }
    public void onDestroy(){}
    //************** client action
    void onConnected(String userId){}
    void onDisconnected(String userId){}

}

package org.games.logic.ddz;

import org.games.model.poker.Card;
import org.games.model.poker.CardsUtil;

import java.util.*;

public class FightTheLandlord {
    interface Logger{
        void warn(Object obj);
    }
    Logger log;
    enum State{
        INIT,PREPARING,PLAYING,OVER
    }
    State state = State.INIT;
    String creatorId;
    List<String> joinedUserId = new ArrayList<>();
    Map<String,CardsInHands> usersCards = new HashMap<>();
    float last;
    float lastJoinTime;
    Runnable ticker;
    String curActiveUserId;
    String curPuttedUserId;
    final List<String> curPassedUserId = new ArrayList<>();
    float curUserStartActiveTime;
    String winnerId;
    float createdTime;
    Runnable timeoutHandler;
    public FightTheLandlord(){
        timeoutHandler = ()->{state = State.OVER;};
        ticker = ()->{
            if(last>30){
                timeoutHandler.run();
                ticker = ()->{};
            }
        };
    }

    //*************** game lifecycle

    void onCreate(){
        if(state==State.INIT){
            createdTime = last;
            timeoutHandler=()->{
                if(last-createdTime>30){
                    state = State.OVER;
                }
            };
            ticker = ()->{
                if(joinedUserId.size()==3){
                    onStart();
                }
            };
            state = State.PREPARING;
        }
    }
    void onStart(){
        if(state==State.PREPARING){
            //notify
            timeoutHandler = ()->{
                if(last-curUserStartActiveTime>30){
                    state=State.OVER;
                }
            };
            ticker=()->{
                for (CardsInHands cards : usersCards.values()) {
                    if(cards.empty()){
                        //notify
                        winnerId = cards.userId;
                        state=State.OVER;
                    }
                }
            };
            state = State.PLAYING;
        }
    }
    void onUpdate(float dt){
        last+=dt;
        ticker.run();
        Optional.ofNullable(timeoutHandler).ifPresent(Runnable::run);
    }
    void onOver(){
        //notify game result
    }
    void onDestroy(){}


    //***************** user action
    void onJoined(){
        if(state==State.PREPARING){
            lastJoinTime = last;
            String joinUserId = "1";
            joinedUserId.add(joinUserId);
            //notify
        }
    }
    void onLeave(){
        if(state==State.PREPARING) {
            String leaveUserId = "1";
            joinedUserId.remove(leaveUserId);
            //notify
        }
    }
    enum Action{
        UNKNOWN,MESSAGE,PUT,PASS
        ;
        static Action from(int action){
            for (Action value : Action.values())
                if(value.ordinal()==action)
                    return value;
            return UNKNOWN;
        }
    }
    //user game action
    final Map<String,Float> senderLastSendTimes = new HashMap<>();
    void onMessage(String senderUserId,String message){
        Float v = senderLastSendTimes.get(senderUserId);
        if(Objects.isNull(v)||(last-v)>5){
            //notify message
            System.out.println(message);
            senderLastSendTimes.put(senderUserId,last);
        }
    }
    Combo lastCombo;
    void onPut(String putUserId,List<Card> cards){
        boolean isCur = Objects.equals(putUserId,curActiveUserId);
        if(!isCur)return;
        if(cards.isEmpty())return;
        Combo n = Combo.from(cards);
        if(!n.isValid())return;
        if(Objects.isNull(lastCombo)){
            //notify
            curPuttedUserId = putUserId;
            lastCombo = n;
        }else{
            if(n.isBiggerThen(lastCombo)){
                //notify
                curPuttedUserId = putUserId;
                lastCombo = n;
                curActiveUserId = nextUserId(curPuttedUserId);
            }else{
                log.warn("invalid put by user id:"+putUserId);
            }
        }
    }
    void onPass(String actionUserId){
        boolean isCur = Objects.equals(actionUserId,curActiveUserId);
        if(!isCur)return;
        curPassedUserId.add(actionUserId);
        if (curPassedUserId.size()==2){//repeat put out
            curActiveUserId = curPuttedUserId;
            curPassedUserId.clear();
            curUserStartActiveTime = last;
            //notify
        }else{//someone passed ,wait next action
            curUserStartActiveTime = last;
            curActiveUserId = nextUserId(actionUserId);
            //notify
        }
    }
    String nextUserId(String curUser){
        int i=0;
        for (String s : joinedUserId) {
            if(Objects.equals(s,curUser)){
                break;
            }
            i++;
        }
        i = (i+1)%joinedUserId.size();
        return joinedUserId.get(i);
    }
    void onAction(String actionUserId,int action,Object... params){
        Action a = Action.from(action);
        switch (a){
            case MESSAGE -> onMessage(actionUserId,String.class.cast(params[0]));
            case PUT -> {
                List<Byte> cast = List.class.cast(params[0]);
                onPut(actionUserId, CardsUtil.byList(cast));
            }
            case PASS -> onPass(actionUserId);
        }
    }
}

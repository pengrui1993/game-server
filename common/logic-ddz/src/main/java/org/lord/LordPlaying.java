package org.lord;

import org.games.logic.ddz.CardsInHands;
import org.games.logic.ddz.Combo;
import org.games.model.poker.Card;
import org.games.model.poker.CardsUtil;

import java.util.*;

public class LordPlaying extends State{
    public LordPlaying(LandLord sm) {
        super(LandLord.State.PLAYING.ordinal(),sm);
        this.sm = sm;
    }
    final LandLord sm;
    Runnable timeoutHandler;
    Runnable ticker;
    float last;
    float curUserStartActiveTime;
    Map<String, CardsInHands> usersCards = new HashMap<>();
    String winnerId;

    SubStateMachine sub = new SubStateMachine();
    @Override
    public void enter(Object... params) {
        last = 0;
        timeoutHandler = ()->{
            if(last-curUserStartActiveTime>30){
                String nextUserId = nextUserId(curActiveUserId);
                if(Objects.equals(nextUserId,curPuttedUserId)){
                    //notify all user passed the combo
                }else{
                    //notify continue putout any combo
                }
                curActiveUserId = nextUserId;
                curUserStartActiveTime = last;
                //notify
            }
        };
        ticker=()->{
            for (CardsInHands cards : usersCards.values()) {
                if(cards.empty()){
                    //notify
                    winnerId = cards.userId;
                    sm.trans(LandLord.State.OVER.ordinal());
                    ticker = ()->{};
                }
            }
        };
    }
    @Override
    public void update(float dt) {
        last+=dt;
        ticker.run();
        Optional.ofNullable(timeoutHandler).ifPresent(Runnable::run);
        sub.update(dt);
    }
    static final int USER_ACTION = 1;
    @Override
    public void event(int id, Object... params) {
        sub.event(id,params);
        if(id==USER_ACTION){
            final Object[] p2 = new Object[params.length - 2];
            System.arraycopy(params,2,p2,0,p2.length);
            onAction(String.class.cast(params[0])
                    ,Integer.class.cast(params[1])
                    ,p2
            );
        }
    }
    //***************** user action
    enum Action{
        UNKNOWN,MESSAGE,PUT,PASS
        ;
        static  Action from(int action){
            for ( Action value :  Action.values())
                if(value.ordinal()==action)
                    return value;
            return UNKNOWN;
        }
    }
    //user game action
    final Map<String,Float> senderLastSendTimes = new HashMap<>();
    String curActiveUserId;
    List<String> joinedUserId = new ArrayList<>();
    String curPuttedUserId;
    final List<String> curPassedUserId = new ArrayList<>();
    void onMessage(String senderUserId,String message){
        Float v = senderLastSendTimes.get(senderUserId);
        if(Objects.isNull(v)||(last-v)>5){
            //notify message
            System.out.println(message);
            senderLastSendTimes.put(senderUserId,last);
        }
    }
    Combo lastCombo;
    void onPut(String putUserId, List<Card> cards){
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
                //log warning
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
                onPut(actionUserId, CardsUtil.byList(List.class.cast(params[0])));
            }
            case PASS -> onPass(actionUserId);
        }
    }
    enum Phrase {
        HELLO,VALUE
    }
    class SubStateMachine extends StateMachine{
        SubStateMachine(){
            transitions.add(new Transition(this,0,Phrase.HELLO.ordinal(),Phrase.VALUE.ordinal()));
            states.addAll(List.of(
                    start = new State(Phrase.HELLO.ordinal(),this){
                        private boolean ok = true;
                        @Override
                        public void enter(Object... params) {
                            super.enter(params);
                        }
                        @Override
                        public void update(float dt) {
                            if(ok){
                                System.out.println("SubStateMachine.update");
                                ok = false;
                            }else{
                                trans(Phrase.VALUE.ordinal());
                            }
                        }
                    }
                    ,end = new State(Phrase.VALUE.ordinal(),this){
                        private boolean ok = true;
                        @Override
                        public void update(float dt) {
                            if(ok){
                                System.out.println("SubStateMachine.update");
                                ok = false;
                            }
                        }
                    })
            );
        }
        @Override
        public StateMachine parent() {
            return sm;
        }
    }
}

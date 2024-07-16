package org.wolf.race;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class FirstRacingPhaser extends MinorPhaser{

    protected final Context ctx;
    final Map<String, Boolean> handsState;
    final List<String> raceUp;
    final List<String> raceDown;
    final List<String> upToDown;
    @Final
    String startUser;
    String curUser;
    boolean talkingCCW;//anticlockwise/counterclockwise and  clockwise
    float timeLimit;
    float last;
    public FirstRacingPhaser(Context ctx, Map<String, Boolean> handsResult) {
        this.ctx = ctx;
        this.handsState = handsResult;
        List<String> list = handsResult.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
        raceUp = new ArrayList<>();
        upToDown = new ArrayList<>();
        ctx.joinedUsers().forEach(s->{if(list.contains(s))raceUp.add(s);});
        raceDown = handsResult.entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();
        timeLimit = 30;
    }
    @Override
    public Minor state() {
        return Minor.TALKING_TO_RACE;
    }
    float curLast;
    @Override
    public void update(float dt) {
        last+=dt;
        curLast+=dt;
        if(curLast>timeLimit){
            next();
        }
    }
    private void next(){
        int index = raceUpIndex(curUser);
        index = talkingCCW?((index-1<0)?raceUp.size()-1:index-1):((index+1)%raceUp.size());
        String next = raceUp.get(index);
        if(Objects.equals(next,startUser)){
            ctx.changeState(new VotingPhaser(ctx,raceUp,raceDown));
        }
        curUser = next;
        curLast = 0;
    }
    @Override
    public void begin() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        startUser = curUser = raceUp.get(r.nextInt(raceUp.size()));
        talkingCCW = r.nextBoolean();
        out.println("current start with counterclockwise:"+talkingCCW);
        last = curLast = 0;
    }

    @Override
    public void event(int type, Object... params) {
        Event event = Event.from(type);
        switch (event){
            case ACTION -> {
                if(params.length<2){
                    out.println("race choice action require(action,sender,yes/no)");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case RACE_HANDS_DOWN -> {
                        final String sender = String.class.cast(params[1]);
                        Boolean b = handsState.get(sender);
                        if(Objects.isNull(b)||!b){
                            out.println("invalid userId");
                            return;
                        }
                        this.handsState.put(sender,false);
                        upToDown.add(sender);
                        //notify
                        out.println(sender+" cancel race");
                        next();
                    }
                }
            }
            case DATA -> {
                if(params.length<2){
                    out.println("missing data ");
                    return;
                }
                final String sender = String.class.cast(params[0]);
                byte[] data = byte[].class.cast(params[1]);
            }
        }
    }
    private int raceUpIndex(String userId){
        for(int i=0;i<raceUp.size();i++){
            if(Objects.equals(raceUp.get(i),userId))return i;
        }
        return -1;
    }
}

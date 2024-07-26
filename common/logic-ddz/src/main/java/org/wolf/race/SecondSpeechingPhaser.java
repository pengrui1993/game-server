package org.wolf.race;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.util.TalkingRoom;
import org.wolf.util.TalkingRoomManager;
import org.wolf.util.WolfBombUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class SecondSpeechingPhaser extends MinorPhaser{

    private final Context ctx;
    final List<String> raceUp;
    final List<String> raceDown;
    @Final
    String startUser;
    String curUser;
    boolean talkingCCW;//anticlockwise/counterclockwise and  clockwise
    float limit;
    float last;
    public SecondSpeechingPhaser(Context ctx, List<String> raceUp, List<String> raceDown) {
        this.ctx = ctx;
        this.raceUp = raceUp;
        this.raceDown = raceDown;
        limit = 15;
        limit = ctx.top().setting.secondSpeechingTimeLimit;
    }
    @Override
    public Minor state() {
        return Minor.SPEECHING;
    }
    float curLast;
    @Override
    public void update(float dt) {
        last+=dt;
        curLast+=dt;
        if(curLast> limit){
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
        out.println("second racing,current start with counterclockwise:"+talkingCCW);
        last = curLast = 0;
        room = TalkingRoomManager.MGR.create(ctx.top().getJoinedUsers());
        room.active(curUser);
    }
    private @Final TalkingRoom room;
    @Override
    public void end() {
        TalkingRoomManager.MGR.destroy(room.joinKey);
        super.end();
    }
    @Override
    public void event(int type, Object... params) {
        Event event = Event.from(type);
        switch (event){
            case ACTION -> {
                if(params.length<1){
                    out.println("seconds race require 1 params");
                    return;
                }
                Action a = Action.from(Integer.class.cast(params[0]));
                switch (a){
                    case RACE_STOP_TALKING->{
                        if(params.length<2){
                            out.println("second race,require 2 params");
                            return;
                        }
                        final String sender = String.class.cast(params[1]);
                        //notify
                        out.println(sender+" cancel race");
                        next();
                    }
                    case WOLF_BOMB ->{
                        if(params.length<2){
                            out.println("race speeching, wolf bomb");
                            return;
                        }
                        WolfBombUtil.handle(ctx.top(),String.class.cast(params[1]));
                    }
                    case TEST_DONE -> {
                        out.println("second racing test enabled");
                        do{
                            next();
                        }while(!Objects.equals(curUser,startUser));
                    }
                }
            }
            case SOUNDS -> {
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

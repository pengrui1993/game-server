package org.games.logic.wolf.race;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.util.TalkingRoom;
import org.games.logic.wolf.util.TalkingRoomManager;
import org.games.logic.wolf.util.WolfBombUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class FirstSpeechingPhaser extends MinorPhaser{

    protected final Context ctx;
    final Map<String, Boolean> handsState;
    final List<String> raceUp;
    final List<String> raceDown;
    final List<String> upToDown;
    @Final
    String startUser;
    String curUser;
    boolean talkingCCW;//anticlockwise/counterclockwise and  clockwise
    float limit;
    float last;
    private @Final TalkingRoom room;
    public FirstSpeechingPhaser(Context ctx, Map<String, Boolean> handsResult) {
        this.ctx = ctx;
        this.handsState = handsResult;
        List<String> list = handsResult.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
        raceUp = new ArrayList<>();
        upToDown = new ArrayList<>();
        ctx.joinedUsers().forEach(s->{if(list.contains(s))raceUp.add(s);});
        raceDown = handsResult.entrySet().stream().filter(e->!e.getValue()).map(Map.Entry::getKey).toList();
        limit = ctx.top().setting.firstSpeechingTimeLimit;
    }
    boolean test;
    @Override
    public Minor state() {
        return Minor.SPEECHING;
    }

    @Override
    public String speaker() {
        return curUser;
    }

    float curLast;
    @Override
    public void update(float dt) {
        last+=dt;
        curLast+=dt;
        if(curLast> limit ||test){
            ctx.changeState(new VotingPhaser(ctx,raceUp,raceDown));
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
    public void end() {
        TalkingRoomManager.MGR.destroy(room.joinKey);
        super.end();
    }

    @Override
    public void begin() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        startUser = curUser = raceUp.get(r.nextInt(raceUp.size()));
        talkingCCW = r.nextBoolean();
        Phaser.out.println("first,current start with counterclockwise:"+talkingCCW);
        last = curLast = 0;
        test = false;
        room = TalkingRoomManager.MGR.create(ctx.top().getJoinedUsers());
        room.active(curUser);
    }

    @Override
    public void event(int type, Object... params) {
        Event event = Event.from(type);
        switch (event){
            case ACTION -> {
                if(params.length<1){
                    Phaser.out.println("first,params require more then 1");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case RACE_HANDS_DOWN -> {
                        if(params.length<2){
                            Phaser.out.println("race hands down action require(action,sender)");
                            return;
                        }
                        final String sender = String.class.cast(params[1]);
                        Boolean b = handsState.get(sender);
                        if(Objects.isNull(b)||!b){
                            Phaser.out.println("invalid userId");
                            return;
                        }
                        this.handsState.put(sender,false);
                        upToDown.add(sender);
                        //notify
                        Phaser.out.println(sender+" cancel race");
                        next();
                    }
                    case RACE_STOP_TALKING->{
                        if(params.length<2){
                            Phaser.out.println("race first speeching require(action,sender)");
                            return;
                        }
                        final String sender = String.class.cast(params[1]);
                        if(!Objects.equals(sender,curUser)){
                            Phaser.out.println("invalid request for stop by:"+sender);
                            return;
                        }
                        //notify
                        Phaser.out.println(sender+" stop talking in first speeching");
                        next();
                    }
                    case WOLF_BOMB ->{
                        if(params.length<2){
                            Phaser.out.println("race speeching, wolf bomb");
                            return;
                        }
                        String wolf = String.class.cast(params[1]);
                        boolean ok = WolfBombUtil.handle(ctx.top(),wolf);
                        if(!ok){
                            out.println("race speeching:wolf bomb action invalid wolf:"+wolf);
                            return;
                        }
                        out.println("race speeching:wolf bomb action ok");
                    }
                    case TEST_DONE -> {
                        Phaser.out.println("first racing test enabled");
                        test = true;
                    }
                }
            }
            case SOUNDS -> {
                if(params.length<2){
                    Phaser.out.println("missing data ");
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

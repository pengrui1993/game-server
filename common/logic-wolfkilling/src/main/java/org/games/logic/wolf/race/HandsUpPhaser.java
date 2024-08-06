package org.games.logic.wolf.race;

import org.games.logic.wolf.core.Action;
import org.games.logic.wolf.core.Final;
import org.games.logic.wolf.core.Event;
import org.games.logic.wolf.core.Minor;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class HandsUpPhaser extends MinorPhaser {
    private final Context ctx;//RacePhaser
    public HandsUpPhaser(Context ctx) {
        this.ctx = ctx;
    }
    //userId :hands up/down
    private final Map<String,Boolean> handsResult = new HashMap<>();
    @Override
    public Minor state() {
        return Minor.HANDS;
    }
    int userSize;
    @Override
    public void begin() {
        last = 0;
        out.println("hands up phaser,please select up or down");
        test = false;
        limit = ctx.top().setting.handsUpTimeoutLimit;
        userSize = ctx.joinedUsers().size();
    }
    boolean test;
    float last;
    @Final
    float limit;
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit||test){
            ctx.joinedUsers()
                    .stream()
                    .filter(e->Objects.isNull(handsResult.get(e)))
                    .forEach(f->handsResult.put(f,false));
        }
        if(handsResult.size()==userSize){
            if(!handsResult.values().stream().filter(c->c).toList().isEmpty())
                ctx.changeState(new FirstSpeechingPhaser(ctx,handsResult));
            else
                ctx.changeState(new DonePhaser(ctx,null));
        }
    }
    @Override
    public void end() {
        out.println("race hands action detail************");
        out.println(handsResult);
        ctx.top().raceInfo.down = handsResult.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey).toList();
        ctx.top().raceInfo.up = handsResult.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
        out.println("hands up phaser exit**************");
    }
    @Override
    public void event(int type, Object... params) {
        Event event = Event.from(type);
        switch (event){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1) {
                    out.println("hands up,require more then one params");
                    return;
                }
                Action a = Action.from(Integer.class.cast(params[0]));
                switch (a){
                    case RACE_CHOICE -> {
                        if(params.length<3){
                            out.println("hands up,race choice action require(action,sender,true/false)");
                            return;
                        }
                        final String sender = String.class.cast(params[1]);
                        if(-1==ctx.top().index(sender)){
                            out.println("hands up,invalid userId:"+sender);
                            return;
                        }
                        boolean yes = Boolean.parseBoolean(String.class.cast(params[2]));
                        out.println("hands up,"+sender+" choose "+yes);
                        this.handsResult.put(sender,yes);
                    }
                    case TEST_DONE->{
                        out.println("hands up test enabled");
                        ThreadLocalRandom r = ThreadLocalRandom.current();
                        handsResult.replaceAll((k, v) -> r.nextBoolean());
                        test=true;
                    }
                }
            }
        }
    }
}

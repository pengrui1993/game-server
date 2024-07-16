package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

class TalkingPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.TALKING;
    }
    private final WolfKilling ctx;
    private @Final boolean orderingCCW;
    private String curUser;
    private @Final String startUser;
    @Final String sergeant;
    float last,curLast;
    TalkingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    final List<String> aliveUser = new ArrayList<>();
    @Final float limit;
    boolean test;
    @Override
    public void begin() {
        sergeant = ctx.sergeant;
        last=curLast=0;
        List<String> living = ctx.roles.entrySet()
                .stream()
                .filter(e->e.getValue().alive())
                .map(Map.Entry::getKey)
                .toList();
        ctx.joinedUsers.forEach(e->{if(living.contains(e))aliveUser.add(e);});
        startUser = curUser = aliveUser.get(ThreadLocalRandom.current().nextInt(aliveUser.size()));
        this.orderingCCW = ctx.talkingOrderingCCW;
        limit = ctx.setting.talkingLimit;
        test = false;
        ctx.curDayTalkingTimes++;
        out.println("talking phaser begin , ordering ccw:"+this.orderingCCW);
    }

    @Override
    public void end() {
        out.println("talking phaser end");
    }

    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case ACTION -> {
                if(params.length<1){
                    out.println("talking phaser action,require 1 params");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case TALKING_NEXT->{
                        if(params.length<2){
                            out.println("talking phaser,talking require 3 params");
                            return;
                        }
                        String sender = String.class.cast(params[1]);
                        if(!Objects.equals(sender,curUser)){
                            out.println("talking phaser,talking next must be current user");
                            return;
                        }
                        next();
                    }
                    case TEST_DONE -> {
                        out.println("talking phaser,test enabled");
                        test = true;
                    }
                }
            }
            case SOUNDS -> {

            }
        }
    }
    @Override
    public void update(float dt) {
        last+=dt;
        curLast+=dt;
        if(curLast>=limit||test){
            next();
        }
    }
    private int index(String uid){
        for(int i=0;i<aliveUser.size();i++){
            if(Objects.equals(aliveUser.get(i),uid))return i;
        }
        return -1;
    }
    private void next() {
        int index = index(curUser);
        index = orderingCCW?
                ((index+1)%aliveUser.size()):
                ((index+aliveUser.size()-1)%aliveUser.size());
        String s = aliveUser.get(index);
        if(Objects.equals(s,startUser)){
            ctx.changeState(new VotingPhaser(ctx));
        }
        curLast = 0;
        curUser = s;
    }
}

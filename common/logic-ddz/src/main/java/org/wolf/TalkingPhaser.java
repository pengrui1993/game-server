package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.util.TalkingRoom;
import org.wolf.util.TalkingRoomManager;
import org.wolf.util.WolfBombUtil;

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
    @Final float limit;
    boolean test;
    private final List<String> aliveUser = new ArrayList<>();
    private @Final TalkingRoom room;
    TalkingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    @Override
    public void begin() {
        sergeant = ctx.sergeant;
        last=curLast=0;
        List<String> living = ctx.roles.entrySet()
                .stream()
                .filter(e->e.getValue().alive())
                .map(Map.Entry::getKey)
                .toList();
        ctx.joinedUsers.stream().filter(living::contains).forEach(aliveUser::add);
        final ThreadLocalRandom r = ThreadLocalRandom.current();
        startUser = curUser = aliveUser.get(r.nextInt(aliveUser.size()));
        this.orderingCCW = ctx.talkingOrderingCCW;
        limit = ctx.setting.talkingLimit;
        test = false;
        ctx.curDayTalkingTimes++;
        room = TalkingRoomManager.MGR.create(ctx.joinedUsers);
        room.active(curUser);
        out.println("talking phaser begin , ordering ccw:"+this.orderingCCW
                +",current speaking user:"+curUser);
    }
    @Override
    public void end() {
        TalkingRoomManager.MGR.destroy(room.joinKey);
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
                    case WOLF_BOMB -> {
                        if(params.length<2){
                            out.println("talking phaser,wolf bomb require 2 params");
                            return;
                        }
                        WolfBombUtil.handle(ctx,String.class.cast(params[1]));
                    }
                    case TEST_DONE -> {
                        out.println("talking phaser,test enabled");
                        test = true;
                        do{
                            next();
                        }while (!Objects.equals(curUser,startUser));
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
            ctx.changeState(new VotingPhaser(ctx));
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
        room.active(curUser);
        out.println("talking,current speaking user:"+curUser);
    }
}

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
    private final boolean orderingCCW;
    private String curUser;
    @Final String startUser;
    @Final String sergeant;
    float last,curLast;
    TalkingPhaser(WolfKilling ctx, boolean orderingCCW) {
        this.ctx = ctx;
        this.orderingCCW = orderingCCW;
    }
    final List<String> aliveUser = new ArrayList<>();
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
    }

    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case ACTION -> {
                if(params.length<3){
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case TALKING_NEXT->{

                    }
                }
            }
        }
    }

    @Override
    public void update(float dt) {
        last+=dt;
        curLast+=dt;
        if(curLast>=30){
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

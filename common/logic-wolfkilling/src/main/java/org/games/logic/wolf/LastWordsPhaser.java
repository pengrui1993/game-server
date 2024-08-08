package org.games.logic.wolf;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.util.WolfBombUtil;

import java.util.Objects;

/*
1，第一天被狼人杀有遗言，白天被票有遗言。
2，被女巫毒死，被猎人带走，其他晚上被狼杀均没有遗言。
 */
class LastWordsPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.LAST_WORDS;
    }
    private final WolfKilling ctx;
    @Final
    boolean first;
    final Runnable change;
    @Override
    public String curLastWorldUser() {
        return diedUserId;
    }

    final String diedUserId;
    LastWordsPhaser(WolfKilling ctx, String whoDied, Runnable change) {
        this.ctx = ctx;
        this.change = change;
        diedUserId = whoDied;
    }
    float last;
    float limit;
    boolean requestCancel;
    boolean test;
    boolean changeRan;
    @Override
    public void begin() {
        last = 0;
        limit = ctx.setting.lastWordsActionTimeoutLimit;
        first = ctx.dayNumber<1;
        test = false;
        requestCancel = false;
        changeRan = false;
        out.println("last word begin");
    }

    @Override
    public void end() {
        ctx.get(diedUserId).goDied();
        out.println("last word end,died "+diedUserId);
    }

    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit||test||requestCancel){
            if(changeRan)return;
            change.run();
            changeRan = true;
        }
    }
    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1){
                    out.println("last world action 1");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case UNKNOWN -> {

                    }
                    case TEST_DONE -> {
                        test = true;
                        out.println("last words, test enabled");
                    }
                    case LAST_WORLD_PASS->{
                        if(params.length<2){
                            out.println("last world action 2");
                            return;
                        }
                        String who = String.class.cast(params[1]);
                        if(!Objects.equals(who,diedUserId)){
                            out.println("invalid request user,must be:"+diedUserId);
                            return;
                        }
                        requestCancel = true;
                        out.println("cancel last world");
                    }
                    case WOLF_BOMB -> {
                        if(params.length<2){
                            out.println("wolf bomb action 2");
                            return;
                        }
                        String wolf = String.class.cast(params[1]);
                        boolean ok = WolfBombUtil.handle(ctx,wolf);
                        if(!ok){
                            out.println("last world:wolf bomb action invalid wolf:"+wolf);
                            return;
                        }
                        out.println("last world:wolf bomb action ok");
                    }
                }
            }
        }
    }
}
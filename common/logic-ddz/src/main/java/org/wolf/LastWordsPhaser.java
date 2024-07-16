package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;

/*
1，第一天被狼人杀有遗言，白天被票有遗言。
2，被女巫毒死，被猎人带走，其他晚上被狼杀均没有遗言。
 */
class LastWordsPhaser extends MajorPhaser{
    @Override
    public Major state() {
        return Major.LAST_WORDS;
    }
    private final WolfKilling ctx;
    @Final
    boolean first;
    final Runnable change;
    final String diedUserId;
    LastWordsPhaser(WolfKilling ctx, String whoDied, Runnable change) {
        this.ctx = ctx;
        this.change = change;
        diedUserId = whoDied;
    }
    float last;
    boolean changed;
    boolean test;
    @Override
    public void begin() {
        last = 0;
        first = ctx.dayNumber<1;
        changed = false;
        test = false;
        out.println("last word begin");
    }

    @Override
    public void update(float dt) {
        last+=dt;
        if((last>5&&!changed)||test){
            change.run();
            changed = true;
        }
    }
    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1)return;
                switch (Action.from(Integer.class.cast(params[0]))){
                    case UNKNOWN -> {

                    }
                    case TEST_DONE -> {
                        test = true;
                        out.println("last words, test enabled");
                    }

                }
            }
        }
    }
}
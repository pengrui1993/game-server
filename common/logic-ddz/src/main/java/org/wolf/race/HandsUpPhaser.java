package org.wolf.race;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void begin() {
        last = 0;
        List<String> users = ctx.joinedUsers();
        for (String user : users) {
            handsResult.put(user,false);
        }
        out.println("hands up phaser,please select up or down");
        test = false;
        limit = ctx.top().setting.handsUpTimeoutLimit;
    }
    boolean test;
    float last;
    @Final float limit;
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit||test){
            ctx.changeState(new FirstRacingPhaser(ctx,handsResult));
        }
    }
    @Override
    public void end() {
        out.println("race hands action result************");
        for (Map.Entry<String, Boolean> e : handsResult.entrySet()) {
            out.println(e.getKey()+":"+e.getValue());
        }
        out.println("hands up phaser exit**************");
    }
    @Override
    public void event(int type, Object... params) {
        Event event = Event.from(type);
        switch (event){
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
                        test=true;
                    }
                }
            }
        }
    }
}

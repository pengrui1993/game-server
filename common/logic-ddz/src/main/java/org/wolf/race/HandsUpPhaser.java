package org.wolf.race;

import org.wolf.action.Action;
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
        out.println("please select up or down");
    }
    float last;
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=5){
            ctx.changeState(new FirstRacingPhaser(ctx,handsResult));
        }
    }
    @Override
    public void end() {
        out.println("race hands action result************");
        for (Map.Entry<String, Boolean> e : handsResult.entrySet()) {
            out.println(e.getKey()+":"+e.getValue());
        }
        out.println("**************");
    }
    @Override
    public void event(int type, Object... params) {
        Event event = Event.from(type);
        switch (event){
            case ACTION -> {
                if(params.length<3){
                    out.println("race choice action require(action,sender,yes/no)");
                    return;
                }
                Action a = Action.from(Integer.class.cast(params[0]));
                switch (a){
                    case RACE_CHOICE -> {
                        final String sender = String.class.cast(params[1]);
                        if(-1==ctx.top().index(sender)){
                            out.println("invalid userId");
                            return;
                        }
                        boolean yes = Boolean.parseBoolean(String.class.cast(params[2]));
                        out.println(sender+" choose "+yes);
                        this.handsResult.put(sender,yes);
                    }
                }
            }
        }
    }
}

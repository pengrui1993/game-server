package org.games.logic.wolf;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.role.Role;
import org.games.logic.wolf.role.Roles;
import org.games.logic.wolf.role.impl.Hunter;

class HunterPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.HUNTER;
    }
    private final WolfKilling ctx;
    final Runnable change;
    HunterPhaser(WolfKilling ctx,Runnable change) {
        this.ctx = ctx;
        this.change = change;
    }
    @Override
    public void end() {
        out.println("hunter phaser end");
    }
    float last;
    boolean choose;
    @Final Hunter hunter;
    boolean test;
    @Override
    public void begin() {
        last=0;
        choose = false;
        hunter = ctx.get(Roles.HUNTER).castTo(Hunter.class);
        test = false;
        limit = ctx.setting.hunterActionTimeoutLimit;
    }
    @Final float limit;
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit||choose||test){
            change.run();
        }
    }
    protected void onAction(Object...params){
        if(params.length<1){
            out.println("hunter action,required more then 1 params");
            return;
        }
        Runnable hunterAction = ()->{
            if(params.length<2){
                out.println("hunter action required 2 params");
                return;
            }
            String sender = String.class.cast(params[1]);
            Role role = ctx.get(sender);
            if(role.role()!=Roles.HUNTER){
                out.println("hunter action,must be hunter send action");
                return;
            }
            if(params.length==2){
                hunter.killedUserId = null;
                choose = true;
                out.println("hunter action,cancel kill");
                return;
            }
            hunter.killedUserId = String.class.cast(params[2]);
            choose = true;
            out.println("hunter action, kill "+hunter.killedUserId);
        };
        switch (Action.from(Integer.class.cast(params[0]))){
            case TEST_DONE -> {
                test = true;
                out.println("hunter phaser test enabled");
            }
            case HUNTER_ACTION -> hunterAction.run();
        }
    }
    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> onAction(params);
        }
    }
}

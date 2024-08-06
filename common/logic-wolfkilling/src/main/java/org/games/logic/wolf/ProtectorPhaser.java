package org.games.logic.wolf;

import org.games.logic.wolf.core.Action;
import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.role.Roles;
import org.games.logic.wolf.role.impl.Protector;
import org.games.logic.wolf.core.Event;

import java.util.Objects;

class ProtectorPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.PROTECTOR;
    }
    private final WolfKilling ctx;
    ProtectorPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    Protector protector;
    String proId;
    float last;
    float limit;
    boolean test;
    String protectedUserId;
    @Override
    public void begin() {
        proId = ctx.getId(Roles.PROTECTOR);
        protector = ctx.get(Roles.PROTECTOR).castTo(Protector.class);
        limit = ctx.setting.protectorActionTimeoutLimit;
        last = 0;
        test = false;
        out.println("protector begin.");
    }
    @Override
    public void end() {
        if(Objects.isNull(protectedUserId)){
            protector.lastProtectedUserId = null;
            ctx.calcCtx.protectedUserId = null;
        }else{
            protector.lastProtectedUserId = protectedUserId;
            ctx.calcCtx.protectedUserId = protectedUserId;
        }
        out.println("protector end.");
    }
    void change(){
        ctx.changeState(new CalcDiedPhaser(ctx));
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(Objects.nonNull(protectedUserId)
                ||last>=limit
                ||test){
            change();
        }
    }
    @Override
    public void event(int type, Object... params) {
        switch(Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1){
                    out.println("protector,1 params");
                    return;
                }
                switch(Action.from(Integer.class.cast(params[0]))){
                    case TEST_DONE -> {
                        test = true;
                        out.println("protector,test enabled");
                    }
                    case PROTECTOR_ACTION->{
                        if(params.length<3){
                            out.println("protector,3 params");
                            return;
                        }
                        String sender = String.class.cast(params[1]);
                        if(!Objects.equals(sender,proId)){
                            out.println("protector,must be protector's action");
                            return;
                        }
                        String protectedUserId = String.class.cast(params[2]);
                        if(Objects.equals(protectedUserId,protector.lastProtectedUserId)){
                            out.println("protector,protect same as yesterday");
                            return;
                        }
                        this.protectedUserId =protectedUserId;
                    }
                }
            }
        }
    }
}

package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.role.Role;
import org.wolf.role.Roles;
import org.wolf.role.impl.*;

import java.util.*;

class PreparingPhaser extends MajorPhaser {
    final List<String> joined = new ArrayList<>();
    static final int LIMIT_PLAYER = 12;
    @Final
    String master;
    boolean prepared;
    float last;
    @Override
    public Major state() {
        return Major.PREPARING;
    }
    private final WolfKilling ctx;
    PreparingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(prepared)ctx.changeState(new WolfPhaser(ctx));
    }
    @Override
    public void begin() {
        master = ctx.master;
        prepared = false;
        last = 0;
    }
    @Override
    public void end() {
        ctx.dayNumber = 0;
    }

    protected void onStart(String who){
        if(Objects.equals(who,master)){
            if(joined.size()==LIMIT_PLAYER){
                List<Roles> list = new ArrayList<>(List.of(
                        Roles.HUNTER
                        ,Roles.PREDICTOR
                        ,Roles.PROTECTOR
                        ,Roles.WITCH
                        ,Roles.WOLF
                        ,Roles.WOLF
                        ,Roles.WOLF
                        ,Roles.WOLF
                        ,Roles.FARMER
                        ,Roles.FARMER
                        ,Roles.FARMER
                        ,Roles.FARMER
                ));
                Collections.shuffle(list);
                for (String s : joined) {
                    ctx.joinedUser.add(s);
                    Roles cur = list.remove(0);
                    switch (cur){
                        case WITCH -> ctx.roles.put(s,new Witch());
                        case HUNTER -> ctx.roles.put(s,new Hunter());
                        case PREDICTOR -> ctx.roles.put(s,new Predictor());
                        case PROTECTOR -> ctx.roles.put(s,new Protector());
                        case WOLF -> ctx.roles.put(s,new Wolf());
                        case FARMER -> ctx.roles.put(s,new Farmer());
                    }
                }
                ctx.changeState(new WolfPhaser(ctx));
                out.println("game start ok");
            }else{
                out.println("must have 12 player to start the game");
            }
        }else{
            out.println("sender is not master,sender:"+who);
        }
    }
    protected void onJoin(String who){
        if(joined.contains(who)){
            out.println("duplicated join game , ignore that,user:"+who);
        }else{
            joined.add(who);
            out.println(who+" join the game");
        }
    }
    protected void onLeft(String who){
        if(joined.remove(who)){
            out.println("left the room:"+who);
        }else{
            out.println("invalid left ,because not exists:"+who);
        }
    }
    @Override
    public void event(int type, Object... params) {
        Event e = Event.from(type);
        if(params.length<1)return;
        switch (e){
            case ACTION -> {
                Action a = Action.from(Integer.class.cast(params[0]));
                if(params.length<2) {out.println("mission sender in "+a); return;}
                String who = String.class.cast(params[1]);
                switch (a){
                    case JOIN -> onJoin(who);
                    case LEFT -> onLeft(who);
                    case START_GAME -> onStart(who);
                }
            }
            case MESSAGE -> {

            }
        }
    }
    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>();
        for(int i=0;i<10;i++)arr.add(i);
        arr.remove((Integer)3);
        arr.remove(0);
        System.out.println(arr.size());
        System.out.println(arr.get(2));
        System.out.println(arr.get(3));
    }
}

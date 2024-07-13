package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Context;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.role.Predictor;
import org.wolf.role.Role;
import org.wolf.role.Witch;
import org.wolf.util.CalcContext;
import org.wolf.util.ConnectionHandler;

import java.util.*;
import static org.wolf.core.Context.*;
/*
flow:
https://gl.ali213.net/html/2017-3/156419.html
 */
public class WolfKilling implements Context<Major,MajorPhaser,WolfKilling> {
    final String id;
    //userId
    final String creator;
    @Final
    String master;

    private MajorPhaser cur;
    @Final List<String> joinedUser = new ArrayList<>();
    @Final Map<String, Role> roles=new HashMap<>();
    boolean gameDone = false;
    int dayNumber;
    private ConnectionHandler connHandler;
    CalcContext calcCtx = new CalcContext();
    public WolfKilling(String id,String creator){
        this.id = id;
        this.creator = creator;
        this.master = creator;
        connHandler = new ConnectionHandler((params)->{

        },(params)->{

        });
        cur = new PreparingPhaser(this);
        cur.begin();
    }
    public boolean isGameOver(){
        return gameDone;
    }
    public int index(String user){
        if(Objects.isNull(user))return -1;
        final List<String> users = this.joinedUser;
        if(users.isEmpty())return -1;
        int val = -1;
        for(int i=0;i<users.size();i++)
            if(Objects.equals(user,users.get(i))){
                val = i;
                break;
            }
        return val;
    }
    @Override
    public void onEvent(int cmd, Object... params) {
        if(cmd==Event.CONNECTION.ordinal())connHandler.handle(params);
        Context.super.onEvent(cmd, params);
    }

    @Override
    public MajorPhaser cur() {
        return this.cur;
    }
    @Override
    public void cur(MajorPhaser s) {
        assert null!=s;
        this.cur = s;
    }
    String witchUserId;
    //TODO
    public Witch getWitch() {
        return roles.get(witchUserId).castTo(org.wolf.role.impl.Witch.class);
    }
    public Predictor getPredictor(){ return ()->true;}
    public List<String> aliveWolf() {
        return Collections.emptyList();
    }


    static final Queue<Runnable> queue = new LinkedList<>();
    static final WolfKilling k = new WolfKilling(UUID.randomUUID().toString(),"user1");
    static long last;
    static final long start = last = now0();
    static void loop(){
        if (now0() - start >= 5000) k.gameDone = true;
        else queue.offer(WolfKilling::loop);
    }
    public static void main(String[] args) {
        List<Runnable> o = List.of(
                () -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user1")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user2")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user3")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.LEFT.ordinal(),"user4")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user4")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.START_GAME.ordinal(),"user1")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user5")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user6")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user7")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user8")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user9")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user10")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user11")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.START_GAME.ordinal(),"user12")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user12")
                ,() -> k.onEvent(Event.ACTION.ordinal(), Action.START_GAME.ordinal(),"user1")

                ,WolfKilling::loop
        );
        queue.addAll(o);
        System.out.println(now0());
        while(!k.isGameOver()){
            //time
            final long tmp = now0();
            final float dt = (last-tmp)/1000.f;
            last = tmp;
            //state
            k.onTick(dt);
            //input
            Optional.ofNullable(queue.poll()).ifPresent(Runnable::run);
            //sleep
            sleep0(10);
        }
        System.out.println(now0());
    }

}

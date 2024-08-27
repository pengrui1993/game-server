package org.games.logic.wolf;

import org.games.logic.wolf.core.Action;
import org.games.logic.wolf.core.Event;
import org.games.logic.wolf.role.Roles;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static org.games.logic.wolf.core.Context.now0;
import static org.games.logic.wolf.core.Context.sleep0;

public class WolfSimpleTest {
    public static void main(String[] args) {
        queue.addAll(o);
        System.out.println(now0());
        while(!k.isGameOver()){
            //time
            final long tmp = now0();
            final float dt = (tmp-last)/1000.f;
            last = tmp;
            //state
            k.onTick(dt);
            //input
            Optional.ofNullable(queue.poll()).ifPresent(Runnable::run);
            //sleep
            sleep0(10);
        }
        k.onDestroy();
        System.out.println(now0());
    }
    static final Queue<Runnable> queue = new LinkedList<>();
    static final WolfKilling k = new WolfKilling(UUID.randomUUID().toString(),"user1");
    static long last;
    static final long start = last = now0();
    static void loop(){
        if (now0() - start >= 1000) {
            if(WolfPhaser.class==k.cur().getClass()){//blocked phaser
                queue.offer(WolfSimpleTest::loop);
                return;
            }
            k.gameDone = true;
            if(k.cur().getClass()!= RacingPhaser.class){
                k.cur().out.println("stop by test at:"+k.cur().getClass());
            }else{
                RacingPhaser rp = RacingPhaser.class.cast(k.cur());
                rp.out.println("stop in racing,sub status:"+rp.cur().getClass());
            }
        }else queue.offer(WolfSimpleTest::loop);
    }
    final Runnable recursive = ()-> {
        Consumer<Class<? extends MajorPhaser>> debugClass = (c)->{
            if (now0() - start >= 1000) {
                if (c != k.cur().getClass()) {
                    k.gameDone = true;
                    return;
                }
                queue.offer(this.recursive);
            }else queue.offer(this.recursive);
        };
        debugClass.accept(WolfPhaser.class);
    };

    static List<Runnable> o = List.of(
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
            ,() -> {// wolf kill emulate
                ThreadLocalRandom r = ThreadLocalRandom.current();
                List<String> users = k.joinedUsers;
                for (String s : k.aliveWolf()) {
                    k.onEvent(Event.ACTION.ordinal(), Action.WOLF_KILL.ordinal(),s,users.get(r.nextInt(users.size())));
                }
            }
            ,()-> k.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal())
            ,()->{
                if(k.cur().getClass()!= WitchPhaser.class)return;
                ThreadLocalRandom r = ThreadLocalRandom.current();
                Runnable kill = ()->{
                    List<String> users = k.joinedUsers;
                    String killed = users.get(r.nextInt(users.size()));
                    if(r.nextBoolean())
                        k.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),k.getId(Roles.WITCH),"kill",killed);
                    else
                        k.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),k.getId(Roles.WITCH),"cancel");
                };
                WitchPhaser wp = WitchPhaser.class.cast(k.cur());
                switch (wp.state){
                    case SAVING -> {
                        if(r.nextBoolean()){
                            k.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),k.getId(Roles.WITCH),"save");
                        }else{
                            k.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),k.getId(Roles.WITCH),"cancel");
                            kill.run();
                        }
                    }
                    case KILLING -> kill.run();
                }
            }

            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.PREDICTOR_ACTION.ordinal(),"user1","user2")
            ,()->{
                ThreadLocalRandom r = ThreadLocalRandom.current();
                String preId = k.getId(Roles.PREDICTOR);
                String anyOneButNotPre;
                while(!Objects.equals(preId,anyOneButNotPre=k.joinedUsers.get(r.nextInt(k.joinedUsers.size()))));
                k.onEvent(Event.ACTION.ordinal(),Action.PREDICTOR_ACTION.ordinal(),preId,anyOneButNotPre);
            }
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.RACE_CHOICE.ordinal(),"123","true")
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.RACE_CHOICE.ordinal(),"user1","true")
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal())//HandsUpPhaser
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal())//FirstRacingPhaser
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.RACE_VOTE.ordinal(),"123","user1")
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.RACE_VOTE.ordinal(),"user1","user1")
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal())//race.VotingPhaser
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal())//SecondRacingPhaser
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.RACE_VOTE.ordinal(),"user2","user1")
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal())//race.VotingPhaser
            ,()->{}//noop for race.done
            ,()-> {if(k.cur().getClass()==PublishDiedInfoPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//PublishDiedInfoPhaser
            ,()-> {if(k.cur().getClass()==LastWordsPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//LastWordsPhaser
            ,()-> {if(k.cur().getClass()==OrderingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//OrderingPhaser
            ,()-> {if(k.cur().getClass()==TalkingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//TalkingPhaser
            ,()-> {if(k.cur().getClass()==VotingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//VotingPhaser
            ,()-> {if(k.cur().getClass()==TalkingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//TalkingPhaser
            ,()->{}
            ,()-> {if(k.cur().getClass()== VotingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.VOTING_VOTE.ordinal(),"user1","user2");}
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal())//VotingPhaser
            ,()-> {if(k.cur().getClass()== LastWordsPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//LastWordsPhaser
            ,()-> {if(k.cur().getClass()== OrderingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//OrderingPhaser
            ,()-> {if(k.cur().getClass()==TalkingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//TalkingPhaser

            , WolfSimpleTest::loop
    );


}

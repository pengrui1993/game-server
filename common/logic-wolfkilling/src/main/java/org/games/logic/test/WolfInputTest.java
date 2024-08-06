package org.games.logic.test;

import org.games.logic.wolf.core.Action;
import org.games.logic.wolf.core.Event;
import org.games.logic.wolf.role.*;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WolfInputTest extends WolfAutoTest{

    @Override
    public void onVoting() {
        AtomicBoolean auto = new AtomicBoolean(autoVoting);
        Set<String> voted = new HashSet<>();
        Runnable gen = ()->{
            ThreadLocalRandom r = ThreadLocalRandom.current();
            app.lived().stream().filter(e->!voted.contains(e)).forEach(u->{
                List<String> lived = app.lived();
                String target = lived.get(r.nextInt(lived.size()));
                app.onEvent(Event.ACTION.ordinal(),Action.VOTING_VOTE.ordinal(),u,target);
            });
        };
        if(auto.get()){
            gen.run();
        }else while(!auto.get()&&voted.size()!=app.lived().size()){
            app.lived().stream().filter(e->!voted.contains(e)).forEach(u->{
                if(auto.get())return;
                out.println(u+" select voting target from:"+app.lived());
                line = s.nextLine().trim();
                switch (line){
                    case "auto"->auto.set(true);
                    case "quit"->System.exit(0);
                    default -> {
                        if(!app.lived().contains(line)){
                            out.println("invalid input:"+line);
                            return;
                        }
                        app.onEvent(Event.ACTION.ordinal(),Action.VOTING_VOTE.ordinal(),u,line);
                        voted.add(u);
                    }
                }
            });
            if(auto.get()){
                gen.run();
            }
        }
        app.onTick(0.1f+app.setting.votingLimit);
    }

    @Override
    public void onWolfInput() {
        AtomicBoolean auto = new AtomicBoolean(autoWolfInput);
        Set<String> wolfs = new HashSet<>();
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Runnable gen = ()-> app.aliveWolf().stream().filter(w->!wolfs.contains(w)).forEach(f->{
            List<String> lived = app.lived();
            String target = r.nextBoolean()?null:lived.get(r.nextInt(lived.size()));
            app.onEvent(Event.ACTION.ordinal(), Action.WOLF_KILL.ordinal(),f,target);
            wolfs.add(f);
        });
        if(auto.get()){
            gen.run();
        }else while(!auto.get()&&app.aliveWolf().size()!=wolfs.size()){
            app.aliveWolf().forEach(w->{
                if(auto.get())return;
                out.println("please enter,killing target[userId],your are:"+w);
                line = s.nextLine().trim();
                switch (line){
                    case "quit"->System.exit(0);
                    case "auto"->auto.set(true);
                    default -> {
                        boolean nil = Objects.equals("null",line);
                        boolean contain = app.lived().contains(line);
                        if(!nil&&!contain){
                            out.println("invalid input for kill:"+line);
                            return;
                        }
                        String target = nil?null:line;
                        app.onEvent(Event.ACTION.ordinal(), Action.WOLF_KILL.ordinal(),w,target);
                        wolfs.add(w);
                    }
                }
            });
        }
        if(auto.get())gen.run();
        app.onTick(0.1f);
    }

    @Override
    public void onWitchInput() {
        Witch witch = app.get(Roles.WITCH, Witch.class);
        String id = app.getId(Roles.WITCH);
        AtomicBoolean auto = new AtomicBoolean(autoWitchInput);
        if(witch.hasMedicine()&&app.wolfKilled()){
            out.println("please enter,[cancel] or [save]");
            line = s.nextLine().trim();
            outer:
            while(true){
                switch (line){
                    case "cancel"-> {
                        app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"cancel");
                        break outer;
                    }
                    case "save"->{
                        app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"save");
                        app.onTick(0.1f);
                        out.println(app.cur().getClass());
                        return;
                    }
                    default -> out.println("unknown command "+line);
                }
            }
        }
        app.onTick(0.1f);
        if(witch.hasDrug()){
            out.println("please enter,[cancel] or [kill userId] or [help]");
            line = s.nextLine().trim();
            while(true){
                switch (line){
                    case "help"-> out.println(app.getRoles());
                    case "cancel"->{
                        app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"cancel");
                        app.onTick(0.1f);
                        out.println(app.cur().getClass());
                        return;
                    }
                    default ->{
                        if(line.startsWith("kill ")){
                            String who = line.split("kill ")[1];
                            if(!app.get(who).alive()){
                                out.println("invalid kill target");
                                continue;
                            }
                            app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"kill",who);
                            app.onTick(0.1f);
                            out.println(app.cur().getClass());
                            return;
                        }else{
                            out.println("unknown command "+line);
                        }
                    }
                }
            }
        }
        ThreadLocalRandom r = ThreadLocalRandom.current();
        boolean first = app.day()<1;
        boolean killed = Objects.nonNull(app.curWolfTarget());
        Runnable randomSaving = ()->{
            if(r.nextBoolean())app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"save");
            else app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"cancel");
        };
        if(auto.get()){
            if(first&&killed){
                randomSaving.run();
            }else{
                if(witch.hasMedicine()&&killed){
                    randomSaving.run();
                }else if(witch.hasDrug()){

                }
            }
        }else while(true){

        }
        app.onTick(0.1f);
    }
    @Override
    public void onPredictorInput() {
        AtomicBoolean auto = new AtomicBoolean(autoPredictorInput);
        String predictor = app.getId(Roles.PREDICTOR);
        Runnable gen = ()->{
            List<String> joinedUsers = app.getJoinedUsers();
            String target = joinedUsers.get(ThreadLocalRandom.current().nextInt(joinedUsers.size()));
            app.onEvent(Event.ACTION.ordinal(),Action.PREDICTOR_ACTION.ordinal(),predictor,target);
        };
        if(auto.get()){
            gen.run();
        }else while(true){
            if(auto.get())break;
            out.println("please choose a uid for validation [userId],predictor uid:"+predictor);
            line=s.nextLine().trim();
            switch (line){
                case "quit"->System.exit(0);
                case "auto"->auto.set(true);
                default -> {
                    if(!app.getJoinedUsers().contains(line)){
                        out.println("invalid input:"+line);
                        continue;
                    }
                    String target = line;
                    app.onEvent(Event.ACTION.ordinal(),Action.PREDICTOR_ACTION.ordinal(),predictor,target);
                }
            }
            break;
        }
        if(auto.get())gen.run();
        app.onTick(0.1f);
    }

    @Override
    public void onRacingHands() {
        boolean random = false;
        Consumer<String> randomAction = (uid)->{
            ThreadLocalRandom r = ThreadLocalRandom.current();
            app.onEvent(Event.ACTION.ordinal(), Action.RACE_CHOICE.ordinal(),uid,r.nextBoolean()+"");
        };
        for (String uid : app.getJoinedUsers()) {
            out.println("roles,"+app.getRoles());
            if(random){
                randomAction.accept(uid);
                continue;
            }
            outer:
            while(true){
                out.println("please choice "+uid+" hands true/false/random/quit");
                line = s.nextLine().trim();
                switch (line){
                    case "true","false"->{
                        app.onEvent(Event.ACTION.ordinal(), Action.RACE_CHOICE.ordinal(),uid,line);
                        break outer;
                    }
                    case "random"-> {
                        randomAction.accept(uid);
                        random=true;
                        break outer;
                    }
                    case "quit"-> System.exit(0);
                    default -> out.println("invalid input:"+line);
                }
            }
        }
        app.onTick(0.1f);
    }
    @Override
    public void onRacingVoting() {
        out.println("start racing voting");
        ThreadLocalRandom lr = ThreadLocalRandom.current();
        final Set<String> set = new HashSet<>();
        final AtomicBoolean auto = new AtomicBoolean(autoRacingVoting);
        Function<String,String> sup = (line)-> switch (line){
            case "quit"-> {System.exit(0);yield null;}
            case "auto"-> {auto.set(true);yield null;}
            case "random"-> app.raceDown().get(lr.nextInt(app.lived().size()));
            default -> {
                if(!app.lived().contains(line)){
                    out.println("invalid input alive user:"+line);
                    yield null;
                }
                yield line;
            }
        };

        Supplier<Boolean> full = ()->app.raceDown().size()==set.size();
        while(!auto.get()&&!full.get()){
            out.println("remain no choice:"+app.raceDown().stream().filter(r->!set.contains(r)).sorted().toList());
            out.println("please choice who send msg:"+set);
            String sender = sup.apply(s.nextLine().trim());
            if(Objects.isNull(sender))continue;
            if(!app.raceDown().contains(sender)){out.println("no down");continue;}
            out.println("please choice who voted msg:");
            String voted = sup.apply(s.nextLine().trim());
            if(Objects.isNull(voted))continue;
            if(!app.raceDown().contains(sender)){out.println("no up");continue;}
            if(sender.equals(voted)){out.println("cannot vote self");continue;}
            app.onEvent(Event.ACTION.ordinal(),Action.RACE_VOTE.ordinal(),sender,voted);
            set.add(sender);
        }
        if(auto.get()&&!full.get()){
            app.raceDown()
                    .stream()
                    .filter(r->!set.contains(r))
                    .sorted()
                    .toList()
                    .forEach(sender-> {
                        final String voted = lr.nextBoolean()?app.raceUp().get(lr.nextInt(app.raceUp().size())):null;
                        app.onEvent(Event.ACTION.ordinal(),Action.RACE_VOTE.ordinal(),sender,voted);
                        set.add(sender);
                    });
        }
        app.onTick(0.1f);//race.DonePhaser.begin
        app.onTick(0.1f);//calc died,begin
        app.onTick(0.1f);//publish info phaser begin
        app.onTick(app.setting.publishDiedInfoPhaserLimit+0.1f);//ordering phaser
    }
    @Override
    public void onOrdering() {
        if(autoOrdering)super.onOrdering();
        else while(true){
            String uid = app.getSergeant();
            out.println("ordering select ccw [true/false] id:"+uid);
            line = s.nextLine().trim();
            switch (line){
                case "quit"-> System.exit(0);
                case "true","false"-> app.onEvent(Event.ACTION.ordinal(), Action.ORDERING_DECISION.ordinal(),uid,line);
                default -> {out.println("unknown cmd:"+line);continue;}
            }
            break;
        }
        app.onTick(0.1f);
    }
    private void dummyHands(){
        out.println(app.minor());
        app.onEvent(Event.ACTION.ordinal(), Action.RACE_CHOICE.ordinal(),"user1","true");
        app.onTick(app.setting.handsUpTimeoutLimit+0.1f);
    }
    @Override
    public WolfAutoTest init() {
        super.onPreparingInput();
        super.onWolfInput();
        super.onWitchInput();
        super.onPredictorInput();
        dummyHands();
        return this;
    }
    static final boolean autoRacingVoting = true;
    static final boolean autoOrdering = true;
    static final boolean autoVoting = true;
    static final boolean autoWolfInput = true;
    static final boolean autoPredictorInput = false;
    static final boolean autoWitchInput = false;
    static boolean auto = false;
    public static void main(String[] args) {
        WolfAutoTest test = auto?new WolfAutoTest(): new WolfInputTest();
        test.init().run();
    }

    public static void main0(String[] args) throws Throwable{
        Robot robot = new Robot();
        Thread.sleep(3000);
        for (char c : "HELLO".toLowerCase().toCharArray()) {
            robot.keyPress(KeyEvent.VK_A+(c-'a'));//b
        }
//        robot.keyPress(KeyEvent.VK_E);
//        robot.keyPress(KeyEvent.VK_E);
        robot.keyPress(KeyEvent.VK_ENTER);
        System.out.println(System.in.read());
        System.out.println(System.in.read());
        System.out.println(System.in.read());
        System.out.println(System.in.read());
        System.out.println(System.in.read());
    }
}

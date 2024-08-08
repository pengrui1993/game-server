package org.games.logic.test;

import org.games.logic.wolf.*;
import org.games.logic.wolf.core.Action;
import org.games.logic.wolf.core.Event;
import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.core.Minor;
import org.games.logic.wolf.role.*;


import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;


public class WolfAutoTest {
    static final WolfKilling app = new WolfKilling(UUID.randomUUID().toString(),"user1");
    static final PrintStream out = System.out;
    static final Scanner s = new Scanner(System.in);
    static String line;
    protected long start;
    public void onProtectorInput(){
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);
    }
    public void onWolfInput(){
        for (String wolfId : app.aliveWolf()) {
            app.onEvent(Event.ACTION.ordinal(), Action.WOLF_KILL.ordinal(),wolfId);
        }
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);//[enter witch phaser|over]
    }
    public void onPreparingInput(){
        for(int i=1;i<=12;i++)
            app.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user"+i);
        app.onEvent(Event.ACTION.ordinal(), Action.START_GAME.ordinal(),"user1");
        app.onTick(0.1f);
        System.out.printf("WolfAutoTest.onPreparingInput,%s,roles:%s",app.cur().getClass(),app.getRoles());
    }
    public void onWitchInput(){
        String uid = app.getId(Roles.WITCH);
        if(app.cur().state()== Major.WITCH){
            app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),uid,"cancel");
            app.onTick(0.1f);
        }
        if(app.cur().state()== Major.WITCH){
            app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),uid,"cancel");
            app.onTick(0.1f);
        }
    }
    public void onPredictorInput(){
        String uid = app.getId(Roles.PREDICTOR);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        String vid = app.get(r.nextInt(app.getJoinedUsers().size()));
        app.onEvent(Event.ACTION.ordinal(), Action.PREDICTOR_ACTION.ordinal(),uid,vid);
        app.onTick(0.1f);
        out.println(app.cur().getClass());
    }
    public void onRacingHands(){
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);
        out.println(app.minor());
    }
    public void onRacingHands1() {
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
    public void onRacingSpeeching(){
        Minor m = app.minor();
        while(m==app.minor()){
            app.onEvent(Event.ACTION.ordinal(), Action.RACE_STOP_TALKING.ordinal(),app.curSpeaker());
            app.onTick(0.1f);
        }
    }
    public void onRacingVoting(){
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);
        out.println(app.minor());

    }
    public void onRacingDone(){
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);
        out.println(app.cur().getClass()+" "+app.minor());
    }
    public void onCalcDied(){
        app.onTick(0.1f);
    }
    public void onDiedInfo(){
        app.onTick(app.setting.publishDiedInfoPhaserLimit+0.1f);//ordering phaser
    }
    public void onLastWords(){
        app.onEvent(Event.ACTION.ordinal(),Action.LAST_WORLD_PASS.ordinal(),app.curLastWorldUser());
        app.onTick(0.1f);//[wolf phaser begin|]
    }
    public void onHunterInput(){
        System.out.println("WolfAutoTest.onHunterInput");
        System.exit(0);
    }
    public void onOrdering(){
        app.onTick(app.setting.orderingLimit+0.1f);//talking phaser begin
    }
    public void onTalking(){
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());//voting begin.
        app.onTick(0.1f);//talking phaser begin
    }
    public void onVoting(){
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);//talking phaser begin
    }
    public final void onRacingInput(){
        if(Objects.isNull(app.minor()))return;
        switch (app.minor()){
            case HANDS -> onRacingHands();
            case SPEECHING -> onRacingSpeeching();
            case VOTING -> onRacingVoting();
            case DONE -> onRacingDone();
        }
    }
    public WolfAutoTest init(){
        return this;
    }
    protected void tick(){
        WolfAutoTest.yield();
        switch (app.cur().state()){
            case PREPARING -> onPreparingInput();
            case WOLF -> onWolfInput();
            case WITCH -> onWitchInput();
            case PREDICTOR -> onPredictorInput();
            case RACE -> onRacingInput();
            case CALC_DIED -> onCalcDied();
            case DIED_INFO ->onDiedInfo();
            case ORDERING -> onOrdering();
            case TALKING -> onTalking();
            case VOTING -> onVoting();
            case PROTECTOR -> onProtectorInput();
            case LAST_WORDS -> onLastWords();
            case HUNTER -> onHunterInput();
            case DONE -> onDone();
            case OVER -> onOver();
        }
    }
    public void onOver(){
        app.onTick(0.1f);
    }
    public void onDone() {
        app.onTick(0.1f);
        out.println("game done last access");
    }
    public void run(){
        start = app.now();
        while(!app.isGameOver()){
           tick();
        }
        app.onDestroy();
        out.println("game run done");
    }
    public static void yield(){
        sleep1();
    }
    public static void sleep1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace(out);
        }
    }
}

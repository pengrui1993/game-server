package org.test;

import org.wolf.Major;
import org.wolf.WolfKilling;
import org.wolf.action.Action;
import org.wolf.evt.Event;
import org.wolf.role.Roles;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class WolfAutoTest {
    static final WolfKilling app = new WolfKilling(UUID.randomUUID().toString(),"user1");
    static final PrintStream out = System.out;
    static final Scanner s = new Scanner(System.in);
    static String line;
    static final boolean userInput = false;

    public void onWolfInput(){
        for (String wolfId : app.aliveWolf()) {
            app.onEvent(Event.ACTION.ordinal(), Action.WOLF_KILL.ordinal(),wolfId);
        }
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);
        out.println(app.cur().getClass());
    }
    public void onPreparingInput(){
        for(int i=1;i<=12;i++)
            app.onEvent(Event.ACTION.ordinal(), Action.JOIN.ordinal(),"user"+i);
        app.onEvent(Event.ACTION.ordinal(), Action.START_GAME.ordinal(),"user1");
        app.onTick(0.1f);
        out.println(app.getRoles());
        out.println(app.cur().getClass());
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
//        app.onEvent(Event.ACTION.ordinal(), Action.RACE_CHOICE.ordinal());
        app.onTick(0.1f);
        out.println(app.minor());
    }
    public void onRacingSpeeching(){
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);
        out.println(app.minor());
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

    public final void onRacingInput(){
        if(Objects.isNull(app.minor()))return;
        switch (app.minor()){
            case VOTING -> onRacingVoting();
            case HANDS -> onRacingHands();
            case DONE -> onRacingDone();
            case SPEECHING -> onRacingSpeeching();
        }
    }
    public void run(){
        while(!app.isGameOver()){
            Thread.yield();
            switch (app.cur().state()){
                case PREPARING -> onPreparingInput();
                case WOLF -> onWolfInput();
                case WITCH -> onWitchInput();
                case PREDICTOR -> onPredictorInput();
                case RACE -> onRacingInput();
            }
        }
    }
}

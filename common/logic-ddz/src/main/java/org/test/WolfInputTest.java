package org.test;

import org.wolf.action.Action;
import org.wolf.evt.Event;
import org.wolf.role.Roles;
import org.wolf.role.Witch;

import java.util.Objects;


public class WolfInputTest extends WolfAutoTest{
    @Override
    public void onWolfInput() {
        out.println("please enter,[kill userId]");
        while(true){
            line = s.nextLine().trim();
            if(Objects.equals("quit",line))break;
            if(Objects.equals("help",line)){
                out.println(app.cur().getClass());
                continue;
            }
            if(line.startsWith("kill ")){
                String who = line.split("kill ")[1].trim();
                if(null==app.get(who)&&!who.isEmpty())continue;
                for (String wolfId : app.aliveWolf()) {
                    app.onEvent(Event.ACTION.ordinal(), Action.WOLF_KILL.ordinal(),wolfId,who.isEmpty()?null:who);
                }
                break;
            }
        }
        app.onEvent(Event.ACTION.ordinal(), Action.TEST_DONE.ordinal());
        app.onTick(0.1f);
        out.println(app.cur().getClass());
    }

    @Override
    public void onWitchInput() {
        Witch witch = app.get(Roles.WITCH, Witch.class);
        String id = app.getId(Roles.WITCH);
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
    }

    @Override
    public void onPredictorInput() {
        String uid = app.getId(Roles.PREDICTOR);
        while(true){
            out.println("please choose a uid for validation [valid userId],predictor uid:"+uid);
            line=s.nextLine().trim();
            if(!line.startsWith("valid ")){
                out.println("invalid input:"+line);
                continue;
            }
            String who = line.split("valid ")[1];
            if(!app.getJoinedUsers().contains(who)){
                out.println("invalid input:"+line);
                continue;
            }
            app.onEvent(Event.ACTION.ordinal(),Action.PREDICTOR_ACTION.ordinal(),uid,who);
            break;
        }
        app.onTick(0.1f);
        out.println(app.cur().getClass());
    }

    @Override
    public void onRacingHands() {
        for (String uid : app.getJoinedUsers()) {
            out.println("roles,"+app.getRoles());
            out.println("please choice "+uid+" hands true/false");
            line = s.nextLine().trim();
            app.onEvent(Event.ACTION.ordinal(), Action.RACE_CHOICE.ordinal(),uid);
        }

    }

    static boolean auto = false;
    public static void main(String[] args) {
        WolfAutoTest test = auto?new WolfAutoTest(): new WolfInputTest();
        test.run();
    }
}

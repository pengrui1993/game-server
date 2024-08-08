package org.games.logic.test;

import org.games.logic.wolf.WolfKilling;
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
    public void onHunterInput() {
        AtomicBoolean auto = new AtomicBoolean(autoHunterInput);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        String hunter = app.getId(Roles.HUNTER);
        if(auto.get()){
            List<String> lived = app.lived();
            String target;
            while(Objects.equals((target=r.nextBoolean()?null:lived.get(r.nextInt(lived.size()))),hunter)){
                out.println("cannot choose itself");
            }
            app.onEvent(Event.ACTION.ordinal(),Action.HUNTER_ACTION.ordinal(),hunter,target);
            app.onTick(0.1f);
            return;
        }
        super.onHunterInput();//TODO
    }

    private void lastWordsPass(){
        super.onLastWords();
    }
    @Override
    public void onLastWords() {
        AtomicBoolean auto = new AtomicBoolean(autoLastWords);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Runnable random = ()->{
            if(r.nextBoolean())lastWordsPass();
            else app.onTick(0.1f+app.setting.lastWordsActionTimeoutLimit);//[wolf phaser begin|]
        };
        if(auto.get()){
            random.run();
            return;
        }
        outer:
        while(!auto.get()){
            out.println("last words action [pass/timeout]:");
            line = s.nextLine().trim();
            switch (line){
                case "pass"->{
                    lastWordsPass();
                    break outer;
                }
                case "timeout"->{
                    app.onTick(0.1f+app.setting.lastWordsActionTimeoutLimit);//[wolf phaser begin|]
                    break outer;
                }
                case "auto"->auto.set(true);
                case "quit"->System.exit(0);
                default -> out.println("invalid cmd:"+line);
            }
        }
        if(auto.get())random.run();
        app.onTick(0.1f);
    }

    @Override
    public void onVoting() {
        AtomicBoolean auto = new AtomicBoolean(autoVoting);
        Set<String> voted = new HashSet<>();
        Runnable gen = ()->{
            ThreadLocalRandom r = ThreadLocalRandom.current();
            app.lived().stream().filter(e->!voted.contains(e)).forEach(u->{
                List<String> list = app.lived();
                String target = r.nextBoolean()?null:list.get(r.nextInt(list.size()));
                app.onEvent(Event.ACTION.ordinal(),Action.VOTING_VOTE.ordinal(),u,target);
                voted.add(u);
            });
        };
        if(auto.get()){
            gen.run();
            app.onTick(0.1f);
            return;
        }
        while(!auto.get()&&voted.size()!=app.lived().size()){
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
        }
        if(auto.get())gen.run();
        app.onTick(0.1f+app.setting.votingLimit);
    }
    @Override
    public void onProtectorInput() {
        super.onProtectorInput();//TODO
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
            app.onTick(0.1f);
            return;
        }
        while(!auto.get()&&app.aliveWolf().size()!=wolfs.size()){
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
        boolean killed = Objects.nonNull(app.curWolfTarget());
        Witch witch = app.get(Roles.WITCH, Witch.class);
        if(!killed&&!witch.hasDrug()){
            out.println("no killed in wolf and no drug, ignore witch input");
            return;
        }
        ThreadLocalRandom r = ThreadLocalRandom.current();
        String id = app.getId(Roles.WITCH);
        AtomicBoolean auto = new AtomicBoolean(autoWitchInput);
        boolean first = app.day()<1;
        final AtomicBoolean cancel = new AtomicBoolean(false);
        final AtomicBoolean done = new AtomicBoolean(false);
        Runnable doSave = ()->{
            app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"save");
            done.set(true);
        };
        Runnable doCancel = ()->{
            app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"cancel");
            if(first)done.set(true);
            if(cancel.get())done.set(true);
            cancel.set(true);
        };
        Consumer<String> doKill = (target)->{
            app.onEvent(Event.ACTION.ordinal(), Action.WITCH_ACTION.ordinal(),id,"kill",target);
            done.set(true);
        };
        Runnable randomSaving = ()->{
            (r.nextBoolean()?doSave:doCancel).run();
            app.onTick(0.1f);
        };
        Runnable randomKilling= ()-> {
            String target = app.lived().get(r.nextInt(app.lived().size()));
            Runnable k = ()->doKill.accept(target);
            (r.nextBoolean()?k:doCancel).run();
            app.onTick(0.1f);
        };
        Runnable autoRun = ()->{
//            boolean noFirstCanSave = !first&&killed&&witch.hasMedicine();
//            boolean noFirstNoSave = !first&&(!killed||!witch.hasMedicine());
//            boolean firstCanSave = first&&killed;
//            if(firstCanSave)randomSaving.run();
//            if(noFirstCanSave)randomSaving.run();
//            if(noFirstNoSave)randomKilling.run();
//            if(cancel.get()&&witch.hasDrug())randomKilling.run();

            if(cancel.get()&&witch.hasDrug()){randomKilling.run();return;}
            if(first&&killed)randomSaving.run();
            if(witch.hasMedicine()&&killed){
                randomSaving.run();
                if(cancel.get()&&witch.hasDrug()){randomKilling.run();}
            }else if(!first&&witch.hasDrug()){
                randomKilling.run();
            }else{
                out.println("invalid state witch phaser must have any drug");
            }
            out.println(app.cur().getClass());//show be phaser change
        };
        Runnable drugInput = ()->{
            label1:
            while(true){
                out.println("witch phaser,do you want killing anyone? [yes/no]");
                switch (line=s.nextLine().trim()){
                    case "no"-> doCancel.run();
                    case "yes"->{
                        label2:
                        while(true){
                            out.println("enter you want to killed id:"+app.lived());
                            String uid =line= s.nextLine().trim();
                            if(app.lived().contains(uid)){
                                out.println("invalid user id:"+uid);
                                doKill.accept(uid);
                                break;
                            }
                            switch (line){
                                case "no"->{doCancel.run();break label2;}
                                case "kill"->{continue label1;}
                                case "quit"->System.exit(0);
                                case "auto"->doKill.accept(app.lived().get(r.nextInt(app.lived().size())));
                            }
                            break;
                        }
                    }
                    case "auto"-> doKill.accept(app.lived().get(r.nextInt(app.lived().size())));
                    case "quit"->System.exit(0);
                    default -> {out.println("invalid cmd"+line);continue;}
                }
                app.onTick(0.1f);
                break;
            }
        };
        Runnable savingInput = ()->{
            boolean needToInput = witch.hasMedicine()&&killed;
            if(!needToInput)return;
            out.println("witch phaser,someone maybe died,do you want saving anyone? [yes/no]");
            switch (line= s.nextLine().trim()){
                case "quit"->System.exit(0);
                case "auto"->{auto.set(true);return;}
                case "yes"-> doSave.run();
                case "no"-> doCancel.run();
                default -> {out.println("invalid cmd"+line);return;}
            }
            app.onTick(0.1f);
        };
        Supplier<Boolean> tryAuto = ()->{
            if(done.get())return true;
            if(auto.get()) {
                autoRun.run();
                app.onTick(0.1f);
                return true;
            }
            return false;
        };
        Supplier<Boolean> savingThenTryAuto = ()->{
            savingInput.run();
            return tryAuto.get();
        };
        if(tryAuto.get())return;
        while(!auto.get()&&!done.get()){
            if(first&&killed&&savingThenTryAuto.get())return;
            boolean noFirstCanSave = !first&&killed&&witch.hasMedicine();
            boolean noSaveCanKill = !first||!killed||!witch.hasMedicine();
            if(noFirstCanSave&&savingThenTryAuto.get())return;
            if(noFirstCanSave&&cancel.get()&&witch.hasDrug()
                    ||(noSaveCanKill&&witch.hasDrug()))drugInput.run();
        }
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
            app.onTick(0.1f);
            return;
        }
        outer:
        while(!auto.get()){
            out.println("predicate choose [userId] to valid,predictor uid:"+predictor);
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
                    break outer;
                }
            }
        }
        if(auto.get())gen.run();
        app.onTick(0.1f);
    }
    @Override
    public void onRacingHands() {
        super.onRacingHands();//TODO
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

    private void timeoutAutoOrdering(){
        super.onOrdering();
    }
    @Override
    public void onOrdering() {
        if(autoOrdering)timeoutAutoOrdering();
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
        if(Objects.isNull(app.minor())){
            out.println("invalid hands");
            return;
        }else{
            out.println("dummy hands:"+app.minor());
        }
        app.onEvent(Event.ACTION.ordinal(), Action.RACE_CHOICE.ordinal(),"user1","true");
        app.onTick(app.setting.handsUpTimeoutLimit+0.1f);
    }

    @Override
    public void onPreparingInput() {
        super.onPreparingInput();//TODO
    }
    @Override
    public WolfAutoTest init() {
        this.onPreparingInput();
        this.onWolfInput();
        this.onWitchInput();
        this.onPredictorInput();
        dummyHands();
        return this;
    }
    @Override
    protected void tick() {
        super.tick();
    }
    static final boolean autoRacingVoting = true;
    static final boolean autoOrdering = true;
    static final boolean autoVoting = true;
    static boolean autoWolfInput = true;
    static boolean autoPredictorInput = true;
    static boolean autoWitchInput = false;
    static boolean autoHunterInput = true;
    static boolean autoLastWords = true;
    static boolean auto = false;

    public static void main(String[] args) {
        //  boolean noSaveCanKill = !first||!killed||!witch.hasMedicine();
        WolfAutoTest test = auto?new WolfAutoTest(): new WolfInputTest();
        test.init().run();
    }
    public static void main3(String[] args) {
        Runnable r = null;
        for(int i=0;i<3;i++){
            Runnable u = ()->{};
            System.out.println(r==u);//false,true,true
            r=u;
        }
        Consumer<String[]> c = WolfInputTest::main3;
        Consumer<String[]> c2 = WolfInputTest::main3;
        assert r != (Runnable)()->{};
        System.out.println(c!=c2);//false
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
    static void bits(int n){
        //System.out.println(-1>>>1);//bit shift
        int v = n;
        int d = 0;
        while(v!=0){
            v = v >>> 1;
            d++;
        }
        System.out.print(d+",");
        for(v=n,d=0;v!=0;){
            v = v >>> 1;
            d++;
        }
        System.out.print(d+" ");

    }
    static void print(boolean[][] arr){
        int row,col;
        for(row=0;row<arr.length;row++){
            for(col=0;col<arr[row].length;col++){
                System.out.print(arr[row][col]+" ");
            }
            System.out.println();
        }
    }
    static boolean[][] bits2(int bit){
        bit = Math.min(bit, 32);
        bit = bit<1?3:bit;//2->4 3->8 4->15
        int row = 2<<bit;
        int col = bit;
        boolean[][] arr = new boolean[row][col];
        for(row=0;row<(2<<bit);row++){//number to col row
            int number = row;
            for(col=0;col<bit;col++){
                arr[row][col] =  ((number>>>col)&0b01)!=0;
            }
        }
//        print(arr);
//        System.out.println();
//        for(row=0;row<arr.length;row++){//col row to number;
//            for(col=0;col<arr[row].length;col++){
//                int number= row;
//                arr[row][col] = ((number>>>col)&0b01)!=0;
//            }
//        }
//        print(arr);
        return arr;
    }
    void bits2(){
        //        bits(0xf);
        boolean[][] bs = bits2(3);
        for(int row=0;row<bs.length;row++){
            boolean[] bi = bs[row];
            boolean c1 = bi[0]&&bi[1]&&bi[2];
            boolean c2 = (!bi[0])||(!bi[1])||(!bi[2]);
            assert !c1==c2;
        }
    }
    static void bits3(boolean[][] arr){
        int row,col;
        for(row=0;row<arr.length;row++){
            for(col=0;col<arr[row].length;col++){
                int rowLen = arr[row].length;
                int val = row*rowLen+col;
                int t = val;
                int i=0;
                while(t!=0){
                    i++;
                    if(i==col){
                        arr[row][col]=0==(t&0x0fe);
                    }
                    t>>>=1;
                }
                System.out.print(val+" ");
            }
        }
    }
}

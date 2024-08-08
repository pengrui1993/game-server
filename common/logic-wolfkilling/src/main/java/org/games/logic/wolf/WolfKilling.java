package org.games.logic.wolf;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.core.Minor;
import org.games.logic.wolf.role.*;
import org.games.logic.wolf.util.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static org.games.logic.wolf.core.Context.*;
/*
flow:
https://gl.ali213.net/html/2017-3/156419.html
 */
public class WolfKilling implements Context<Major,MajorPhaser,WolfKilling> {
    final String id;//instance id
    final String creator; //userId
    public final long createdTime = now();
    @Final
    String master;
    private MajorPhaser cur;
    @Final
    List<String> joinedUsers = new ArrayList<>();
    @Final
    Map<String, Role> roles=new HashMap<>();
    boolean gameDone = false;
    Team winner = Team.NIL;
    public final Setting setting = new Setting();
    int curDayTalkingTimes;
    int dayNumber;
    private ConnectionHandler connHandler;
    final CalcContext calcCtx = new CalcContext();
    public final RaceInfo raceInfo = new RaceInfo();
    public final DeadInfo deadInfo = new DeadInfo();
    boolean talkingOrderingCCW;
    private final Map<Roles,String> singleRolesId = new HashMap<>();
    public @Final SessionInterface sessions = SessionInterface.dummy;
    @Final
    String sergeant;
    public WolfKilling(String id,String creator){
        this.id = id;
        this.creator = master =creator;
        connHandler = new ConnectionHandler((params)->{

        },(params)->{

        });
        cur = new PreparingPhaser(this);
        cur.begin();
    }
    @Read
    public boolean isGameOver(){
        return gameDone;
    }
    @Read
    public int index(String user){
        if(Objects.isNull(user))return -1;
        final List<String> users = this.joinedUsers;
        if(users.isEmpty())return -1;
        int val = -1;
        for(int i=0;i<users.size();i++)
            if(Objects.equals(user,users.get(i))){
                val = i;
                break;
            }
        return val;
    }
    @Read
    public String next(String user){
        int index = index(user);
        if(-1==index)return null;
        return joinedUsers.get((index+1)%joinedUsers.size());
    }

    @Read
    public String pre(String user){
        int index = index(user);
        if(-1==index)return null;
        if(index==0)return joinedUsers.get(joinedUsers.size()-1);
        return joinedUsers.get(index-1);
    }
    @Override
    public void onEvent(int cmd, Object... params) {
        switch (Event.from(cmd)){
            case NULL -> cur().out.println("room null event");
            case CONNECTION -> connHandler.handle(params);
            case CONFIG -> setting.change(params);
            case ACTION -> {
                if(params.length<1)break;

            }
        }
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

    @Override
    public void changeState(MajorPhaser next) {
        sessions.notifyStateChange(getRoles(),cur.state(),next.state());
        Context.super.changeState(next);
    }

    @Write
    public void putRole(Roles r, String uid) {
        if(cur.getClass()== PreparingPhaser.class){
            singleRolesId.put(r,uid);
        }else{
            cur.out.println("ignore put , must be preparing phaser do that");
        }
    }
    @Write
    public void setGameOver(){
        gameDone = true;
    }
    @Read
    public String getId(Roles role){
        return singleRolesId.get(role);
    }
    @Read
    public Role get(Roles role){
        return roles.get(getId(role));
    }
    @Read
    public <T extends Role> T get(Roles r,Class<T> c){
        return get(r).castTo(c);
    }
    @Read
    public Role get(String id){
        return Optional.ofNullable(roles.get(id)).orElse(NoneRole.NONE);
    }
    public String get(int index){
        if(index<0||index>joinedUsers.size()-1)return null;
        return joinedUsers.get(index);
    }
    @Read
    public List<String> aliveWolf() {
        return roles.entrySet()
                .stream()
                .filter(e->e.getValue().role()== Roles.WOLF&&e.getValue().alive())
                .map(Map.Entry::getKey)
                .toList();
    }
    private List<String> aliveByType(RoleType t){
        return roles.entrySet()
                .stream()
                .filter(e->e.getValue().role().type==t)
                .filter(e->e.getValue().alive())
                .map(Map.Entry::getKey)
                .toList();
    }
    @Read
    public List<String>aliveFramer(){
        return aliveByType(RoleType.FRM);
    }
    @Read
    public List<String>aliveHero(){
        return aliveByType(RoleType.HEO);
    }
    @Read
    public List<String> lived() {
        return roles.entrySet()
                .stream()
                .filter(e->e.getValue().alive())
                .map(Map.Entry::getKey)
                .toList();
    }
    @Read
    public List<String> raceDown(){
        return raceInfo.down;
    }
    @Read
    public List<String> raceUp(){
        return raceInfo.up;
    }
    @Read//uid
    public String getSergeant(){
        return sergeant;
    }
    @Read
    public String curLastWorldUser(){
        return cur.curLastWorldUser();
    }
    @Read
    public String curWolfTarget(){
        return calcCtx.killingTargetUserId;
    }
    @Read
    public int day(){
        return dayNumber;
    }
    @Read
    public boolean teamAllDied(Team team) {
        return roles.values()
                .stream()
                .filter(Role::alive)
                .map(Role::role)
                .filter(f -> f.team == team)
                .toList().isEmpty();
    }
    @Read
    public boolean typeAllDied(RoleType type) {
        return roles.values()
                .stream()
                .filter(Role::alive)
                .map(Role::role)
                .filter(f -> f.type == type)
                .toList().isEmpty();
    }

    @Read
    public boolean anyTeamWinner() {
        return typeAllDied(RoleType.FRM)
                || typeAllDied(RoleType.HEO)
                || typeAllDied(RoleType.WLF);
    }
    static final Queue<Runnable> queue = new LinkedList<>();
    static final WolfKilling k = new WolfKilling(UUID.randomUUID().toString(),"user1");
    static long last;
    static final long start = last = now0();
    static void loop(){
        if (now0() - start >= 1000) {
            if(WolfPhaser.class==k.cur().getClass()){//blocked phaser
                queue.offer(WolfKilling::loop);
                return;
            }
            k.gameDone = true;
            if(k.cur().getClass()!=RacingPhaser.class){
                k.cur.out.println("stop by test at:"+k.cur().getClass());
            }else{
                RacingPhaser rp = RacingPhaser.class.cast(k.cur());
                rp.out.println("stop in racing,sub status:"+rp.cur().getClass());
            }
        }else queue.offer(WolfKilling::loop);
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
            ,()-> {if(k.cur.getClass()==PublishDiedInfoPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//PublishDiedInfoPhaser
            ,()-> {if(k.cur.getClass()==LastWordsPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//LastWordsPhaser
            ,()-> {if(k.cur.getClass()==OrderingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//OrderingPhaser
            ,()-> {if(k.cur.getClass()==TalkingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//TalkingPhaser
            ,()-> {if(k.cur.getClass()==VotingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//VotingPhaser
            ,()-> {if(k.cur.getClass()==TalkingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//TalkingPhaser
            ,()->{}
            ,()-> {if(k.cur.getClass()== VotingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.VOTING_VOTE.ordinal(),"user1","user2");}
            ,()-> k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal())//VotingPhaser
            ,()-> {if(k.cur.getClass()== LastWordsPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//LastWordsPhaser
            ,()-> {if(k.cur.getClass()== OrderingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//OrderingPhaser
            ,()-> {if(k.cur.getClass()==TalkingPhaser.class)
                k.onEvent(Event.ACTION.ordinal(),Action.TEST_DONE.ordinal());}//TalkingPhaser

            ,WolfKilling::loop
    );

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

    @Override
    public void onDestroy() {
        cur().out.println("winner:"+winner);
        Context.super.onDestroy();
    }

    public void setSergeant(String sergeant) {
        if(cur.getClass()== RacingPhaser.class){
            this.sergeant = sergeant;
        }else{
            cur.out.println("invalid set sergeant");
        }
    }
    public List<String> getJoinedUsers(){
        return Collections.unmodifiableList(joinedUsers);
    }
    public Map<String,Role> getRoles(){
        return Collections.unmodifiableMap(roles);
    }
    public boolean wolfKilled(){
        return Objects.nonNull(calcCtx.killingTargetUserId);
    }
    public Minor minor(){
        return cur.minor();
    }
    public MajorPhaser newWolfPhaser() {
        return new WolfPhaser(this);
    }
    public String curSpeaker(){
        return cur.speaker();
    }

}

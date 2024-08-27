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
    private final ConnectionHandler connHandler;
    final CalcContext calcCtx = new CalcContext();
    public final RaceInfo raceInfo = new RaceInfo();
    public final DeadInfo deadInfo = new DeadInfo();
    boolean talkingOrderingCCW;
    private final Map<Roles,String> singleRolesId = new HashMap<>();
    @Override
    public SessionInterface sessionManager() {
        return SessionInterface.dummy;
    }
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
        sessionManager().notifyStateChange(getRoles(),cur.state(),next.state());
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
    @Override
    public void onDestroy() {
        cur().out.println("winner:"+winner);
        Context.super.onDestroy();
    }

    @Override
    public SpeakingRoomManager talkingRoomManager() {
        return TalkingRoomManager.MGR;
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

package org.games.logic.wolf;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.role.Role;
import org.games.logic.wolf.role.Roles;
import org.games.logic.wolf.role.impl.*;

import java.util.*;
import java.util.stream.Collectors;

class PreparingPhaser extends MajorPhaser {
    final List<String> joined = new ArrayList<>();
    static final int LIMIT_PLAYER = 12;
    @Final
    String master;
    boolean test;
    boolean started;
    float last;
    float limit;
    private @Final List<Roles> roles;
    final Map<String,Roles> userTarget = new HashMap<>();
    @Override
    public Major state() {
        return Major.PREPARING;
    }
    private final WolfKilling ctx;
    PreparingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    void change(){
        ctx.sessions.notifyStart(ctx.getRoles());
        ctx.changeState(new WolfPhaser(ctx));
        started = false;
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(started||test){
            defaultRole();
            change();
        }else if(last>limit){
            randomRemainRoles();
            change();
        }
    }
    @Override
    public void end() {
        out.println("preparing,show all players role: ******************");
        out.println(ctx.roles);
        out.println("preparing,done ******************");
    }

    @Override
    public void begin() {
        master = ctx.master;
        test = false;
        started = false;
        last = 0;
        limit = ctx.setting.preparingActionTimeoutLimit;
        roles = new ArrayList<>(List.of(
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
    }

    void randomRemainRoles(){
        if(userTarget.isEmpty()){
            defaultRole();
            return;
        }
        final List<String> joinedUser = new ArrayList<>(joined);
        final Map<String, Role> roles = new HashMap<>();
        final Map<String,Roles> rolesEnum = new HashMap<>();
        final List<Roles> consumed = new ArrayList<>();
        List<Roles> list = this.roles;
        Map<Roles, List<String>> roleUsers = userTarget.entrySet()
                .stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue()
                        .stream()
                        .map(Map.Entry::getKey)
                        .toList()));

        roleUsers.forEach((key, value) -> {
            switch (key) {
                case NONE -> out.println("invalid foreach in preparing");
                case WOLF,FARMER ->{
                    int min = Math.min(value.size(),4);
                    for(int i=0;i<min;i++){
                        consumed.add(key);
                        final String uid = value.get(i);
                        rolesEnum.put(uid,key);
                        switch (key){
                            case WOLF -> roles.put(uid,new Wolf(ctx));
                            case FARMER -> roles.put(uid,new Farmer(ctx));
                        }
                    }
                }
                default -> {
                    if(value.isEmpty())return;
                    final String uid = value.get(0);
                    rolesEnum.put(uid,key);
                    consumed.add(key);
                    switch (key){
                        case WITCH -> roles.put(uid,new Witch(ctx));
                        case PREDICTOR -> roles.put(uid,new Predictor(ctx));
                        case PROTECTOR -> roles.put(uid,new Protector(ctx));
                        case HUNTER -> roles.put(uid,new Hunter(ctx));
                    }
                    ctx.putRole(key,uid);
                }
            }
        });
        list.removeAll(consumed);
        joinedUser.removeAll(rolesEnum.keySet());
        joinedUser.forEach(uid->{
            Roles key;
            switch (key=list.remove(0)){
                case WITCH -> roles.put(uid,new Witch(ctx));
                case PREDICTOR -> roles.put(uid,new Predictor(ctx));
                case PROTECTOR -> roles.put(uid,new Protector(ctx));
                case HUNTER -> roles.put(uid,new Hunter(ctx));
                case WOLF -> roles.put(uid,new Wolf(ctx));
                case FARMER -> roles.put(uid,new Farmer(ctx));
            }
            rolesEnum.put(uid,key);
            if(!(key==Roles.WOLF||key==Roles.FARMER))ctx.putRole(key,uid);
        });
        ctx.dayNumber = 0;
        ctx.roles = Collections.unmodifiableMap(roles);
        ctx.joinedUsers = List.copyOf(joined);
        out.println("preparing,user->role "+rolesEnum);
    }
    void defaultRole(){
        final Map<String, Role> roles = new HashMap<>();
        List<Roles> list = this.roles;
        Collections.shuffle(list);
        for (String uid : joined) {
            Roles key;
            switch (key=list.remove(0)){
                case WITCH -> roles.put(uid,new Witch(ctx));
                case HUNTER -> roles.put(uid,new Hunter(ctx));
                case PREDICTOR -> roles.put(uid,new Predictor(ctx));
                case PROTECTOR -> roles.put(uid,new Protector(ctx));
                case WOLF -> roles.put(uid,new Wolf(ctx));
                case FARMER -> roles.put(uid,new Farmer(ctx));
            }
            if(!(key==Roles.WOLF||key==Roles.FARMER))ctx.putRole(key,uid);
        }
        ctx.dayNumber = 0;
        ctx.roles = Collections.unmodifiableMap(roles);
        ctx.joinedUsers = List.copyOf(joined);
    }

    protected void onStart(String who){
        if(!Objects.equals(who,master)){
            out.println("sender is not master,sender:"+who);
            return;
        }
        if(joined.size()!=LIMIT_PLAYER){
            out.println("must have 12 player to start the game");
            return;
        }
        started = true;
    }

    protected void onJoin(String who){
        if(joined.contains(who)){
            out.println("duplicated join game , ignore that,user:"+who);
        }else{
            joined.add(who);
            ctx.sessions.notifyJoin(who,joined);
        }
    }
    protected void onLeft(String who){
        if(joined.remove(who)){
            ctx.sessions.notifyLeft(who,joined);
        }else{
            out.println("invalid left ,because not exists:"+who);
        }
    }
    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<2) {
                    out.println("preparing action,required 2 params");
                    return;
                }
                String who = String.class.cast(params[1]);
                switch (Action.from(Integer.class.cast(params[0]))){
                    case JOIN -> onJoin(who);
                    case LEFT -> onLeft(who);
                    case START_GAME -> onStart(who);
                }
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

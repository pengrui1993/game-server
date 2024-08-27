package org.games.logic.wolf;

import org.games.logic.wolf.core.*;
import org.games.logic.wolf.role.Roles;
import org.games.logic.wolf.role.Witch;

import java.util.*;
import java.util.stream.Collectors;

class WolfPhaser extends MajorPhaser {
    private final WolfKilling ctx;
    WolfPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    private boolean firstTimes;
    //wolfUserId to userId
    private @Final Map<String,String> wolfSelectResult;
    private @Final Set<String> livedWolf;
    private float last;
    //userId
    private String targetResult;
    private @Final float limit;
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
        wolfSelectResult = new HashMap<>();
        livedWolf = new HashSet<>(ctx.aliveWolf());
        targetResult = null;
        test = false;
        last = 0;
        limit = ctx.setting.wolfActionTimeoutLimit;
        if(!firstTimes)ctx.deadInfo.print(out);
        ctx.sessionManager().notifyWolfAction(ctx.getJoinedUsers());
        out.println("wolf phaser begin");
    }
    boolean test;
    private void change(){
        final Runnable changer = ()-> {
            if(firstTimes&&Objects.nonNull(targetResult)){
                ctx.changeState(new WitchPhaser(ctx));
                return;
            }
            final Witch w = ctx.get(Roles.WITCH, Witch.class);
            boolean witchCanAction = w.alive()&&w.hasAnyMedicine();
            boolean predictorCanAction = ctx.get(Roles.PREDICTOR).alive();;
            boolean protectorCanAction = ctx.get(Roles.PROTECTOR).alive();
            if(predictorCanAction){
                ctx.changeState(new PredictorPhaser(ctx));
            }else if(witchCanAction){
                ctx.changeState(new WitchPhaser(ctx));
            }else if(protectorCanAction){
                ctx.changeState(new ProtectorPhaser(ctx));
            }else{
                ctx.changeState(new CalcDiedPhaser(ctx));
            }
        };
        final List<String> selectedId = wolfSelectResult.values().stream().filter(Objects::nonNull).toList();
        if(selectedId.isEmpty()){
            targetResult = null;
            changer.run();
            return;
        }
        // Map<String, List<String>> collect = selectedId.stream().collect(Collectors.groupingBy(s -> s));
        selectedId.stream()
                .collect(Collectors.groupingBy(s -> s))
                .entrySet()
                .stream()
                .map(e->Map.entry(e.getKey(),e.getValue().size()))
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .ifPresent(v->{
                    String whoBeKilled = targetResult = v.getKey();
                    out.printf("%s got %s,be killed\n",whoBeKilled,v.getValue());
                    changer.run();
                });
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit
                ||livedWolf.size()==wolfSelectResult.size()
                ||test){
           change();
        }
    }
    @Override
    public void event(int type, Object... params) {
        Event e = Event.from(type);
        if(params.length<1)return;
        switch (e){
            case ACTION -> {
                int action = Integer.class.cast(params[0]);
                Action a = Action.from(action);
                switch (a){
                    case WOLF_KILL -> {
                        if(params.length<2) {out.println("wolf kill action mission action sender"); return;}
                        String wolf = String.class.cast(params[1]);
                        String target = params.length>=3?String.class.cast(params[2]): null;
                        if(Objects.isNull(wolf))return;//target is null:empty king
                        if(!livedWolf.contains(wolf)){
                            out.println(wolf+" is not wolf");
                            return;
                        }
                        final String pre = wolfSelectResult.get(wolf);
                        wolfSelectResult.put(wolf,target);
                        ctx.sessionManager().notifyWolfSelect(ctx.aliveWolf(),wolf,target,pre);
                    }
                    case TEST_DONE ->{
                        out.println("wolf phaser test enabled");
                        test = true;
                    }
                }
            }
            case MESSAGE -> {

            }
        }
    }
    @Override
    public void end() {
        out.println("wolfs vote result:"+wolfSelectResult);
        ctx.calcCtx.clear();
        ctx.calcCtx.killingTargetUserId = targetResult;
    }

    @Override
    public Major state() {
        return Major.WOLF;
    }

    public static void main(String[] args) {
        String s = String.class.cast(null);
        System.out.println(s);//null
    }
}

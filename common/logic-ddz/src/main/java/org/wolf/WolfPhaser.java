package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;
import org.wolf.role.Roles;
import org.wolf.role.Witch;

import java.util.*;
import java.util.stream.Collectors;

class WolfPhaser extends MajorPhaser {
    private final WolfKilling ctx;
    WolfPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    private boolean firstTimes;
    //wolfUserId to userId
    private final Map<String,String> wolfSelectResult = new HashMap<>();
    private float last;
    //userId
    private String result;
    private @Final float limit;
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
        for (String s : ctx.aliveWolf()) {
            wolfSelectResult.put(s,null);
        }
        out.println("wolf phaser begin,lived wolfs "+wolfSelectResult);
        test = false;
        limit = ctx.setting.wolfActionTimeoutLimit;
    }
    boolean test;
    private void change(){
        final Runnable changer = ()-> {
            if(firstTimes){
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
        final Collection<String> selectedId = wolfSelectResult.values().stream().filter(Objects::nonNull).toList();
        if(selectedId.isEmpty()){
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
                    String whoBeKilled = result = v.getKey();
                    out.printf("%s got %s,be killed\n",whoBeKilled,v.getValue());
                    changer.run();
                });
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(Objects.nonNull(result))return;
        if(last>=limit||test){
           change();
        }
    }
    void wolfSelect(String wolf,String target){
        if(Objects.isNull(wolf))return;
        if(!wolfSelectResult.containsKey(wolf)){
            out.println(wolf+" is not wolf");
            return;
        }
        final String pre = wolfSelectResult.get(wolf);
        wolfSelectResult.put(wolf,target);
        out.printf("wolf:%s select %s, pre selected:%s\n",wolf,target,pre);
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
                        String who = String.class.cast(params[1]);
                        String target = params.length>=3?String.class.cast(params[2]): null;
                        wolfSelect(who,target);
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
        ctx.calcCtx.killingTargetUserId = result;
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

package org.wolf;

import org.wolf.action.Action;
import org.wolf.evt.Event;

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
    private float enterPhaserTime;
    //userId
    private String result;
    @Override
    public void begin() {
        firstTimes = ctx.dayNumber<1;
        enterPhaserTime=last = 0;
        for (String s : ctx.aliveWolf()) {
            wolfSelectResult.put(s,null);
        }
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(Objects.nonNull(result))return;
        if(last-enterPhaserTime>=15){
            final Runnable changer = ()->{
                if(firstTimes){
                    ctx.changeState(new WitchPhaser(ctx));
                    return;
                }
                boolean witchCanAction = ctx.getWitch().alive()&&ctx.getWitch().hasAnyMedicine();
                boolean predictorCanAction = ctx.getPredictor().alive();
                boolean protectorCanAction = ctx.getPredictor().alive();
                if(predictorCanAction){
                    ctx.changeState(new PredictorPhaser(ctx));
                }else if(witchCanAction){
                    ctx.changeState(new WitchPhaser(ctx));
                }else if(protectorCanAction){
                    ctx.changeState(new ProtectorPhaser(ctx));
                }else{
                    ctx.changeState(new CalcActionPhaser(ctx));
                }
            };
            final Collection<String> selectedId = wolfSelectResult.values();
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
                    int count = v.getValue();
                    out.printf("%s got %s,be killed\n",whoBeKilled,count);
                    changer.run();
                });
        }
    }
    void wolfSelect(String wolf,String target){
        if(Objects.isNull(out))return;
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
                }

            }
        }
    }
    @Override
    public void end() {
        if(Objects.isNull(result)){
            out.println("empty kill result");
        }else{
            out.println(result+" died");
        }
    }

    @Override
    public Major state() {
        return Major.WOLF;
    }

    public static void main(String[] args) {
        String s = String.class.cast(null);
        System.out.println(s);
    }
}

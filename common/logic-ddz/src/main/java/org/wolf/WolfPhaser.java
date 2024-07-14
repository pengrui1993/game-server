package org.wolf;

import org.wolf.action.Action;
import org.wolf.evt.Event;
import org.wolf.util.ChangeStateUtil;

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
        out.println("wolfs "+wolfSelectResult);
    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(Objects.nonNull(result))return;
        if(last-enterPhaserTime>=5){
            out.println("wolf kill phaser timeout");
            final Runnable changer = ()-> ChangeStateUtil.change(ctx,firstTimes
                    ,()->new WitchPhaser(ctx)
                    ,()->new PredictorPhaser(ctx)
                    ,()->new ProtectorPhaser(ctx)
                    ,()->new CalcActionPhaser(ctx)
            );
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
                    int count = v.getValue();
                    out.printf("%s got %s,be killed\n",whoBeKilled,count);
                    changer.run();
                });
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
                }

            }
        }
    }
    @Override
    public void end() {
        out.println("vote result:"+wolfSelectResult);
        ctx.calcCtx.clear();
        ctx.calcCtx.killingTargetUserId = result;
        if(Objects.isNull(result)){
            out.println("empty kill result");
        }else{
            out.println(result+" will be killed");
        }
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

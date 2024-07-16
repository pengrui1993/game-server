package org.wolf;

import org.wolf.action.Action;
import org.wolf.core.Final;
import org.wolf.evt.Event;

import java.util.*;
import java.util.stream.Collectors;

class VotingPhaser extends MajorPhaser {
    @Override
    public Major state() {
        return Major.VOTING;
    }
    private final WolfKilling ctx;
    VotingPhaser(WolfKilling ctx) {
        this.ctx = ctx;
    }
    boolean test;
    private float last;
    float limit;
    final Map<String,String> livedVote = new HashMap<>();
    @Final boolean talkingAgain;
    @Override
    public void begin() {
        test = false;
        last = 0;
        limit = ctx.setting.votingLimit;
        ctx.lived().forEach(u->livedVote.put(u,null));
        talkingAgain = ctx.curDayTalkingTimes==1;
    }

    private void calcVoting(){
        List<String> abandon = livedVote.entrySet()
                .stream()
                .filter(e -> Objects.isNull(e.getValue()))
                .map(Map.Entry::getKey).toList();
        Map<String, List<String>> collect = livedVote.entrySet()
                .stream()
                .filter(e -> Objects.nonNull(e.getValue()))
                .map(f -> Map.entry(f.getValue(), f.getKey()))
                .collect(Collectors.groupingBy(Map.Entry::getKey))
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().stream().map(Map.Entry::getValue).toList()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        ArrayList<Map.Entry<String, List<String>>> entries = new ArrayList<>(collect.entrySet());
        entries.sort(Comparator.comparingInt(o -> o.getValue().size()));
        if(entries.isEmpty()){

        }else if(entries.size()==1){

        }else{
            String diedUserId = null;
            if(talkingAgain){
                ctx.changeState(new TalkingPhaser(ctx));
            }else{
                ctx.changeState(new LastWordsPhaser(ctx,diedUserId,()->{

                }));
                ctx.curDayTalkingTimes = 0;
            }
        }

    }
    @Override
    public void update(float dt) {
        last+=dt;
        if(last>=limit||test){
            calcVoting();
        }
    }

    @Override
    public void event(int type, Object... params) {
        switch (Event.from(type)){
            case NULL -> {}
            case ACTION -> {
                if(params.length<1){
                    out.println("voting,require 1 params ");
                    return;
                }
                switch (Action.from(Integer.class.cast(params[0]))){
                    case TEST_DONE -> {
                        test = true;
                        out.println("voting test enabled");
                    }
                    case VOTING_VOTE -> {}
                }
            }
        }
    }
}
